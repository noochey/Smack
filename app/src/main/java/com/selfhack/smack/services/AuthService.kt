package com.selfhack.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.selfhack.smack.utilities.URL_LOGIN
import com.selfhack.smack.utilities.URL_REGISTER
import org.json.JSONObject
import com.android.volley.AuthFailureError
import com.selfhack.smack.utilities.URL_ADD_USER
import org.json.JSONException


object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener { response ->
            println(response)
            complete(true)
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not register user: $error")
            complete(false)
        }) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=uft-8"
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener { response ->

            try {
                authToken = response.getString("token")
                userEmail = response.getString("user")
                isLoggedIn = true
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)
            }

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not log in user: $error")
            complete(false)
        }) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=uft-8"
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }
        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(context: Context, name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("name", name)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()

        val createRequest = object : JsonObjectRequest(Method.POST, URL_ADD_USER, null, Response.Listener { response ->
            try {
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.id = response.getString("_id")
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)
            }

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not add user: $error")
            complete(false)
        }) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=uft-8"
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["Authorization"] = "Bearer $authToken"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(createRequest)
    }

}