package com.yashendra.todoapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.yashendra.todoapp.MainActivity
import com.yashendra.todoapp.Utills.Utills
import com.yashendra.todoapp.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val intent= Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            Utills.hideSoftKeyBoard(this, binding.root)
            val email=binding.etEmail.text.toString()
            val password=binding.etPassword.text.toString()
            if(isvalid()){
//               Utills.showSnackBar(binding.root, "Register Successfully")
                LoginUser(email, password)
            }
        }
    }

    private fun LoginUser(email: String, password: String) {
        binding.progressbarLogin.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this@LoginActivity)
        val api = "https://fantastic-cyan-katydid.cyclic.app/api/todo/auth/login"

        // Create JSON object for parameters
        val params = JSONObject()
        params.put("email", email)
        params.put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST, api, params,
            { response ->

                binding.progressbarLogin.visibility = View.GONE
                // Handle the JSON response if needed
                try {
                    if (response.getBoolean("success")) {
                        val token = response.getString("token")
                        val userObject = response.getJSONObject("user")
                        val userId = userObject.getString("_id")
                        val username = userObject.getString("username")
                        val userEmail = userObject.getString("email")
                        val userAvatar = userObject.getString("avatar")

                        // Now you can use the token and user information as needed
                        Utills.showSnackBar(binding.root, "Login Successfully")
                        Log.d("token is", "token is $token")
                        //now store the token in shared preference
                        Utills.storeToken(this@LoginActivity, token)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        intent.putExtra("token", token)
                        intent.putExtra("userId", userId)
                        finish()



                    } else {
                        // Handle cases where registration was not successful
                        Toast.makeText(this@LoginActivity, response.getString("msg"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("tag", "error is " + error?.message)
                binding.progressbarLogin.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        )

        // Add the request to the request queue
        queue.add(request)
    }

    private fun isvalid(): Boolean {
        if(binding.etEmail.text.toString().isEmpty()){
            Utills.showSnackBar(binding.root, "Please Enter Email")
            return false
        }
        if(binding.etPassword.text.toString().isEmpty()){
            Utills.showSnackBar(binding.root, "Please Enter Password")
            return false
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        if(Utills.getToken(this) != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}