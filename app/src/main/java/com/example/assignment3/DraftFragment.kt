package com.example.assignment3

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
    lateinit var myDataSet: Array<PromptOutput>
    lateinit var datasource: Datasource
    lateinit var draftOutputRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_draft, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datasource = Datasource()

        draftOutputRecycler = view.findViewById<RecyclerView>(R.id.draftOutputRecycler)
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

    private fun parseResponse(): Array<String> {
        val numOfDrafts = 3
        val titleLength = "EMAIL DRAFT".length
        val draftList = Array<String>(numOfDrafts) { "" }
        var currentDraftTitleStart: Int
        var currentDraftStart: Int
        var nextDraftStart = 0


        for (index in 0 until numOfDrafts) {
            currentDraftTitleStart = promptOutput.indexOf("EMAIL DRAFT", nextDraftStart - 1)
            Log.d("Output Parsing", "EMAIL DRAFT contains: " + promptOutput.contains("EMAIL DRAFT"))
            currentDraftStart = nextNonSpaceIndex(currentDraftTitleStart + titleLength)
            if (index == numOfDrafts - 1) {
                draftList[index] = promptOutput.substring(currentDraftStart)
                Log.d("Output Parsing", "draftList[" + index + "]: " + draftList[index])
            } else {
                nextDraftStart = promptOutput.indexOf("EMAIL DRAFT", currentDraftStart + titleLength)

                draftList[index] = promptOutput.substring(currentDraftStart, nextDraftStart)
                Log.d("Output Parsing", "draftList[" + index + "]: " + draftList[index])
            }

            Log.d("Output Parsing", "currentDraftTitleStart: " + currentDraftTitleStart +
                    ", currentDraftStart: " + currentDraftStart +
                    ", nextDraftStart: " + nextDraftStart)
        }

        return draftList
    }

    private fun nextNonSpaceIndex(startIndex: Int) : Int {
        var currIndex = startIndex
        var endIndex = 0
        var isNonSpaceFound = false
        while (!isNonSpaceFound) {
            if (promptOutput[currIndex] != ' '
                && promptOutput[currIndex] != '1'
                && promptOutput[currIndex] != '2'
                && promptOutput[currIndex] != '3'
                && promptOutput[currIndex] != ':'
                && promptOutput[currIndex] != 'n'
                && promptOutput[currIndex] != '\\'
                && promptOutput[currIndex] != '-')
            {
                endIndex = currIndex
                isNonSpaceFound = true
            } else {
                currIndex++
            }
        }
        return endIndex
    }

    private fun updateRecycler(newList : Array<String>) {
        datasource.importPromptOutputs(newList.toList())
        draftOutputRecycler.adapter = ItemAdapter(requireContext(), datasource.loadPromptOutputs())
    }
}