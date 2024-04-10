package com.example.assignment3

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.databinding.FragmentDraftBinding
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class DraftFragment : Fragment() {

    lateinit var promptOutput: String
    lateinit var draftList: Array<String>
    private lateinit var datasource: Datasource
    private lateinit var draftOutputRecycler: RecyclerView
    private lateinit var binding: FragmentDraftBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDraftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //tracks what the current background color is
        var isColorWhite = false

        //adds menu buttons specifically for menu fragment
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    //changes background color
                    R.id.settingsButton -> {
                        //changes background and reassigns isColorWhite
                        isColorWhite = if (isColorWhite) {
                            //color is white, so changes to black and updates isColorWhite
                            view.setBackgroundColor(Color.parseColor("#222222"))
                            !isColorWhite
                        } else {
                            //color is black, so changes to white and updates isColorWhite
                            view.setBackgroundColor(Color.parseColor("#EBEBEB"))
                            !isColorWhite
                        }
                        true
                    }
                    android.R.id.home -> view.findNavController().popBackStack()
                    else -> {
                        true
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        //creates datasource object to hold recycler data
        datasource = Datasource()

        //grabs recycler and sets an adapter to it
        draftOutputRecycler = binding.draftOutputRecycler
        draftOutputRecycler.adapter = ItemAdapter(requireContext(), datasource.loadPromptOutputs())
        draftOutputRecycler.setHasFixedSize(true)

        //grabs the inputs for the chatgpt response from the previous fragments
        val promptInput = DraftFragmentArgs.fromBundle(requireArguments()).promptInput
        //assigns respective elements from prompt input into variables
        val prompt = promptInput[0]
        val tone = promptInput[1]
        val address = promptInput[2]


        getResponse(prompt, tone, address)
    }

    private fun getResponse(prompt: String, tone: String, address: String) {
        //sets up the client with 50 second timeouts since chatgpt takes a bit to respond
        val client = OkHttpClient.Builder()
            .connectTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .build()

        //json format
        val mediaType = "application/json".toMediaType()

        //json chatgpt request
        val body = ("{\r" +
                "\n    \"messages\": [\r" +
                "\n        {\r" +
                "\n            \"role\": \"user\",\r" +
                "\n            \"content\": \"Create three email drafts based off this prompt: " + prompt + ". The tone " +
                                        "of this email is " + tone + ". The email is addressed to " + address + ". Each draft " +
                                        "must say *EMAIL DRAFT* above it. Instead of writing the sender's name, just write a placeholder" +
                                        " [sender's name]\"\r" +
                "\n        }\r" +
                "\n    ],\r" +
                "\n    \"web_access\": false,\r" +
                "\n    \"system_prompt\": \"\",\r" +
                "\n    \"temperature\": 0.9,\r" +
                "\n    \"top_k\": 5,\r" +
                "\n    \"top_p\": 0.9,\r" +
                "\n    \"max_tokens\": 2000\r" +
                "\n}").toRequestBody(mediaType)

        //builds request to make call with
        val request = Request.Builder()
            .url("https://open-ai21.p.rapidapi.com/conversationgpt35")
            .post(body)
            .addHeader("content-type", "application/json")
            .addHeader("X-RapidAPI-Key", "0f8f78a214msh663ff0c74b61a1cp1781d3jsn977f1e2764fb")
            .addHeader("X-RapidAPI-Host", "open-ai21.p.rapidapi.com")
            .build()

        //grabs response or failure from client
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API Failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("error", "API Response unsuccessful")
                }

                //response from chatgpt
                val responseBody = response.body?.string()

                val gson = Gson()
                if (responseBody != null) {
                    try {
                        //decodes json into kotlin object
                        Log.d("Response", responseBody)
                        val aiResponse = gson.fromJson(responseBody, AIResponse::class.java)
                        //runs on thread to edit ui elements
                        activity?.runOnUiThread {
                            //stores result of ai prompt
                            promptOutput = aiResponse.result
                            Log.d("Prompt Output", promptOutput)
                            //parses for list of strings
                            draftList = parseResponse()
                            updateRecycler(draftList)
                        }
                    }
                    catch (e: JsonSyntaxException){
                        Log.e("error", "Json Syntax error", e)
                    }
                } else {
                    Log.e("error", "no response body")
                }
            }
        })
    }

    //parses response of chat gpt and returns as an array of email drafts as strings
    private fun parseResponse(): Array<String> {
        //custom draft size
        val numOfDrafts = 3
        //length of text chat gpt puts before every draft
        val titleLength = "EMAIL DRAFT".length
        //empty list to hold drafts
        val draftList = Array<String>(numOfDrafts) { "" }
        //start of the current draft title
        var currentDraftTitleStart: Int
        //start of current draft content
        var currentDraftStart: Int
        //start of the next draft, init to zero to start
        var nextDraftStart = 0


        //iterates through text and grabs each email draft generated by chatGPT
        //we told chat gpt to have a very specific format so we can reliably parse though the text
        for (index in 0 until numOfDrafts) {
            //grabs start of the title of the email draft
            currentDraftTitleStart = promptOutput.indexOf("EMAIL DRAFT", nextDraftStart - 1)

            //finds the next space in the current draft that isn't a space or some strange formatting
            currentDraftStart = nextNonSpaceIndex(currentDraftTitleStart + titleLength)

            //handles edge case of parsing final draft
            if (index == numOfDrafts - 1) {
                //sets the the last draft and doesn't bother finding the next draft start
                draftList[index] = promptOutput.substring(currentDraftStart)
            } else {
                //finds the start of the next draft
                nextDraftStart = promptOutput.indexOf("EMAIL DRAFT", currentDraftStart + titleLength)

                //sets the draft to the array
                draftList[index] = promptOutput.substring(currentDraftStart, nextDraftStart)
            }
        }

        return draftList
    }

    //finds the next nonspace/weird formatting character in the string
    private fun nextNonSpaceIndex(startIndex: Int) : Int {
        var currIndex = startIndex
        var endIndex = 0

        //loops through the string while boolean is false
        var isNonSpaceFound = false
        while (!isNonSpaceFound) {
            //list of characters to skip
            if (promptOutput[currIndex] != ' '
                && promptOutput[currIndex] != '1'
                && promptOutput[currIndex] != '2'
                && promptOutput[currIndex] != '3'
                && promptOutput[currIndex] != ':'
                && promptOutput[currIndex] != 'n'
                && promptOutput[currIndex] != '\\'
                && promptOutput[currIndex] != '-')
            {
                //found actual character, gets index and ends loop
                endIndex = currIndex
                isNonSpaceFound = true
            } else {
                //look at next index in string
                currIndex++
            }
        }
        return endIndex
    }

    //updates the recycler with the new info parsed by chat gpt
    private fun updateRecycler(newList : Array<String>) {
        //loads new data into the datasource object
        datasource.importPromptOutputs(newList.toList())
        //resassigns the adapter of the recycler view to new adapter object with new data set
        draftOutputRecycler.adapter = ItemAdapter(requireContext(), datasource.loadPromptOutputs())
    }
}