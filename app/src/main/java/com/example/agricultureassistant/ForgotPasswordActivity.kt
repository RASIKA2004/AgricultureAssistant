package com.example.agricultureassistant

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var emailEditText: EditText
    private lateinit var resetPasswordButton: Button
    private lateinit var backToLoginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailEditText)
        resetPasswordButton = findViewById(R.id.resetPasswordButton)
        backToLoginText = findViewById(R.id.backToLoginText)

        resetPasswordButton.setOnClickListener {
            sendPasswordResetEmail()
        }

        backToLoginText.setOnClickListener {
            finish()
        }
    }

    private fun sendPasswordResetEmail() {

        val email = emailEditText.text.toString().trim()

        if (email.isEmpty()) {
            emailEditText.error = "Please enter your email"
            emailEditText.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Please enter a valid email"
            emailEditText.requestFocus()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Password reset link sent to your email",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    exception.message ?: "Failed to send reset link",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}