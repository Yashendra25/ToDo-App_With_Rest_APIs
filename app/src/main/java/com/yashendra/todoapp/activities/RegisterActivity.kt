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
import com.yashendra.todoapp.databinding.ActivityRegisterBinding
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.registerBtn.setOnClickListener {
            //hide keyboard
            Utills.hideSoftKeyBoard(this, binding.root)
            val name=binding.etName.text.toString()
            val email=binding.etEmail.text.toString()
            val password=binding.etPassword.text.toString()
            if(isvalid()){
//               Utills.showSnackBar(binding.root, "Register Successfully")
                Registeruser(name, email, password)
            }
        }
    }

    private fun Registeruser(name: String, email: String, password: String) {
        binding.progressbarLogin.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this@RegisterActivity)
        val api = "https://fantastic-cyan-katydid.cyclic.app/api/todo/auth/register"

        // Create JSON object for parameters
        val params = JSONObject()
        params.put("username", name)  // Update this line to use "username" instead of "name"
        params.put("email", email)
        params.put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST, api, params,
            { response ->

//                Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
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
                        Utills.showSnackBar(binding.root, "Register Successfully")
                        Log.d("token is", "token is $token")
                        // Example: Save the token to SharedPreferences
                        Utills.storeToken(this@RegisterActivity, token)

                        // Continue with further actions or navigation
                    } else {
                        // Handle cases where registration was not successful
                        Toast.makeText(this@RegisterActivity, response.getString("msg"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("tag", "error is " + error?.message)
                binding.progressbarLogin.visibility = View.GONE
                Toast.makeText(this@RegisterActivity, "Registration Failed", Toast.LENGTH_SHORT).show()
            }
        )

        // Add the request to the request queue
        queue.add(request)
    }


    private fun isvalid(): Boolean {
        if (binding.etName.text.toString().isEmpty()) {
            Utills.showSnackBar(binding.root, "Please enter name")
            return false
        } else if (binding.etEmail.text.toString().isEmpty()) {
            Utills.showSnackBar(binding.root, "Please enter email")
            return false
        } else if (binding.etPassword.text.toString().isEmpty()) {
            Utills.showSnackBar(binding.root, "Please enter password")
            return false
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        if (Utills.getToken(this) != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}