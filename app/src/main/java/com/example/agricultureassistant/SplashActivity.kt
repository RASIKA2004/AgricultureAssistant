package com.example.agricultureassistant

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({

            if (auth.currentUser != null) {

                // User is already logged in
                startActivity(
                    Intent(this, MainActivity::class.java)
                )

            } else {

                // No user is logged in
                startActivity(
                    Intent(this, LoginActivity::class.java)
                )
            }

            finish()

        }, 1500)
    }
}