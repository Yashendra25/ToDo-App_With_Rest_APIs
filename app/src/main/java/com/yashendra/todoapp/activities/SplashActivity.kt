package com.yashendra.todoapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yashendra.todoapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if(supportActionBar != null)
            supportActionBar?.hide()
        val background = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(3000)
                    val intent = android.content.Intent(baseContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }
}