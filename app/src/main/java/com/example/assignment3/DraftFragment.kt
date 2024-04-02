package com.example.assignment3

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    lateinit var draftDisplay: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_draft, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val promptInput = DraftFragmentArgs.fromBundle(requireArguments()).promptInput
        val prompt = promptInput[0]
        val tone = promptInput[1]
        val address = promptInput[2]

        draftDisplay = view.findViewById(R.id.draftDisplay)
        draftDisplay.text = "Loading"

        getResponse(prompt, tone, address)
    }

    private fun getResponse(prompt: String, tone: String, address: String) {
        val client = OkHttpClient.Builder()
            .connectTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .build()

        val mediaType = "application/json".toMediaType()

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

        val request = Request.Builder()
            .url("https://open-ai21.p.rapidapi.com/conversationgpt35")
            .post(body)
            .addHeader("content-type", "application/json")
            .addHeader("X-RapidAPI-Key", "0f8f78a214msh663ff0c74b61a1cp1781d3jsn977f1e2764fb")
            .addHeader("X-RapidAPI-Host", "open-ai21.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API Failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("error", "API Response unsuccessful")
                }

                val responseBody = response.body?.string()

                val gson = Gson()
                if (responseBody != null) {
                    try {
                        Log.d("Response", responseBody)
                        val aiResponse = gson.fromJson(responseBody, AIResponse::class.java)
                        activity?.runOnUiThread {
                            promptOutput = aiResponse.result
                            parseResponse()
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

    private fun parseResponse() {

    }
}