package com.example.agricultureassistant

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var forgotPasswordText: TextView
    private lateinit var registerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Connect XML views with Kotlin
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        progressBar = findViewById(R.id.loginProgressBar)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)
        registerText = findViewById(R.id.registerText)

        // Login button
        loginButton.setOnClickListener {
            loginUser()
        }

        // Forgot password
        forgotPasswordText.setOnClickListener {
            startActivity(
                Intent(this, ForgotPasswordActivity::class.java)
            )
        }

        // Create account
        registerText.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }

    private fun loginUser() {

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validate email
        if (email.isEmpty()) {
            emailEditText.error = "Please enter your email"
            emailEditText.requestFocus()
            return
        }

        // Validate password
        if (password.isEmpty()) {
            passwordEditText.error = "Please enter your password"
            passwordEditText.requestFocus()
            return
        }

        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters"
            passwordEditText.requestFocus()
            return
        }

        // Show loading
        setLoading(true)

        // Firebase login
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                setLoading(false)

                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Welcome back, Farmer! 🌱",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Login successful → Dashboard
                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun setLoading(isLoading: Boolean) {

        if (isLoading) {

            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false
            loginButton.text = "PLEASE WAIT..."

        } else {

            progressBar.visibility = View.GONE
            loginButton.isEnabled = true
            loginButton.text = "LOGIN"
        }
    }
}