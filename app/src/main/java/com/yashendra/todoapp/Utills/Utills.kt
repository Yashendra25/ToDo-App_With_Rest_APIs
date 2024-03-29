package com.yashendra.todoapp.Utills

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.yashendra.todoapp.R
import org.json.JSONException
import org.json.JSONObject

object Utills {
    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }
    //showing snackbar
    fun showSnackBar(view: View, message: String) {
        try {
            val snackbar = com.google.android.material.snackbar.Snackbar.make(
                view,
                message,
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
            )
            snackbar.show()
        } catch (e: Exception) {

        }
    }
    //code for shared preference storing of token
    fun storeToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }
    //code for shared preference getting of token
    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }
    //code for storing data using post request
    fun postRequest(context: Context, url: String, body: HashMap<String, String>, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val jsonObject = JSONObject(body as Map<*, *>)

        val request = object : JsonObjectRequest(Method.POST, url, jsonObject,
            { response ->
                Log.d("TAG", "postRequest: $response")
                if (response.getBoolean("success")) {
                    onSuccess.invoke()
                } else {
                    onError.invoke("Unsuccessful response from server")
                }
            },
            { error ->
                Log.d("TAG", "postRequest: $error")
                onError.invoke(error.message ?: "Unknown error occurred")
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                // Set content type
                headers["Content-Type"] = "application/json"
                // Add authorization header with token
                getToken(context)?.let { token ->
                    headers["Authorization"] = "$token"
                    Log.d("TAG", "getHeaders: $token")
                }
                return headers
            }
        }

        requestQueue.add(request)
    }

    //code for getting data using get request and the function is returning the response that is used in HomeFragment
    fun getRequest(context: Context, url: String, onSuccess: (response: JSONObject) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)

        val request = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                Log.d("TAG", "getRequest: $response")
                onSuccess.invoke(response)
            },
            { error ->
                Log.d("TAG", "getRequest: $error")
                onError.invoke(error.message ?: "Unknown error occurred")

            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                // Set content type
                headers["Content-Type"] = "application/json"
                // Add authorization header with token
                getToken(context)?.let { token ->
                    headers["Authorization"] = "$token"
                    Log.d("TAG", "getHeaders: $token")
                }
                return headers
            }
        }

        requestQueue.add(request)

    }

    //code for getting a random color and setting it for further in card view list item in recylerview
    //the function is short line and generate random color hex code
    fun getRandomColor(): String {
        val chars = "0123456789ABCDEF"
        var color = "#"
        for (i in 1..6) {
            color += chars[Math.floor(Math.random() * 16).toInt()]
        }
        return color
    }

    //code for upadting the task

    fun updateTaskToAPI(requireContext: Context, id: String, titleText: String, descriptionText: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val url: String = "https://fantastic-cyan-katydid.cyclic.app/api/todo/$id"
        val body = HashMap<String, String>()
        body["title"] = titleText
        body["description"] = descriptionText
        val requestQueue = Volley.newRequestQueue(requireContext)
        val jsonObject = JSONObject(body as Map<*, *>)

        val request = object : JsonObjectRequest(Method.PUT, url, jsonObject,
            { response ->
                Log.d("TAG", "putRequest: $response")
                if (response.getBoolean("success")) {
                    onSuccess.invoke()
                } else {
                    onError.invoke("Unsuccessful response from server")
                }
            },
            { error ->
                Log.d("TAG", "postRequest: $error")
                onError.invoke(error.message ?: "Unknown error occurred")
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                // Set content type
                headers["Content-Type"] = "application/json"
                // Add authorization header with token
                getToken(requireContext)?.let { token ->
                    headers["Authorization"] = "$token"

                }
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun deleteTaskFromAPI(requireContext: Context, url: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val requestQueue = Volley.newRequestQueue(requireContext)

        val request = object : JsonObjectRequest(Method.DELETE, url, null,
            { response ->
                Log.d("TAG", "deleteRequest: $response")
                if (response.getBoolean("success")) {

                    onSuccess.invoke()

                } else {
                    onError.invoke()
                }
            },
            { error ->
                Log.d("TAG", "deleteRequest: $error")
                onError.invoke()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                // Set content type
                headers["Content-Type"] = "application/json"
                // Add authorization header with token
                getToken(requireContext)?.let { token ->
                    headers["Authorization"] = "$token"
                }
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun finishTask(requireContext: Context, id: String, onSuccess:  (String) -> Unit, onError:  (String) -> Unit) {

        val url: String = "https://fantastic-cyan-katydid.cyclic.app/api/todo/$id"
        val body = HashMap<String, String>()
        body["finished"] = "true"
        val requestQueue = Volley.newRequestQueue(requireContext)
        val jsonObject = JSONObject(body as Map<*, *>)

        val request = object : JsonObjectRequest(Method.PUT, url, jsonObject,
            { response ->
                Log.d("TAG", "putRequest: $response")
                if (response.getBoolean("success")) {
                    onSuccess.invoke("Task Finished Successfully")
                } else {
                    onError.invoke("Unsuccessful response from server")
                }
            },
            { error ->
                Log.d("TAG", "postRequest: $error")
                onError.invoke(error.message ?: "Unknown error occurred")
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                // Set content type
                headers["Content-Type"] = "application/json"
                // Add authorization header with token
                getToken(requireContext)?.let { token ->
                    headers["Authorization"] = "$token"

                }
                return headers
            }
        }

        requestQueue.add(request)
    }
}







