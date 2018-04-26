package com.selfhack.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.selfhack.smack.controller.App
import com.selfhack.smack.model.Channel
import com.selfhack.smack.model.Message
import com.selfhack.smack.utilities.URL_GET_CHANNELS
import com.selfhack.smack.utilities.URL_GET_MESSAGES
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit) {
        clearChannels()
        val channelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->

            try {
                for (x in 0 until response.length()) {
                    val channel = response.getJSONObject(x)
                    val name = channel.getString("name")
                    val desc = channel.getString("description")
                    val id = channel.getString("_id")
                    val newChannel = Channel(name, desc, id)
                    this.channels.add(newChannel)
                }
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not find channels: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=uft-8"
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["Authorization"] = "Bearer ${App.prefs.authToken}"
                return headers
            }
        }

        App.prefs.requestQueue.add(channelRequest)
    }

    fun getMessages(channelId : String, complete: (Boolean) -> Unit) {
        clearMessages()
        val messageRequest = object : JsonArrayRequest(Method.GET, "$URL_GET_MESSAGES$channelId", null, Response.Listener { response ->

            try {
                for (x in 0 until response.length()) {
                    val message = response.getJSONObject(x)
                    val messageBody = message.getString("messageBody")
                    val userName = message.getString("userName")
                    val id = message.getString("_id")
                    val channelId = message.getString("channelId")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val timeStamp = message.getString("timeStamp")

                    val newMessage = Message(messageBody, id, channelId, userName, userAvatar, userAvatarColor, timeStamp)
                    this.messages.add(newMessage)
                }
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not find messages: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=uft-8"
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["Authorization"] = "Bearer ${App.prefs.authToken}"
                return headers
            }
        }

        App.prefs.requestQueue.add(messageRequest)
    }

    fun clearMessages(){
        this.messages.clear()
    }

    fun clearChannels(){
        this.channels.clear()
    }

}