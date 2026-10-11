package com.example.agricultureassistant

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText

    private lateinit var registerButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var backButton: ImageButton
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.registerEmailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        passwordEditText = findViewById(R.id.registerPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)

        registerButton = findViewById(R.id.registerButton)
        progressBar = findViewById(R.id.registerProgressBar)
        backButton = findViewById(R.id.backButton)
        loginText = findViewById(R.id.loginText)

        backButton.setOnClickListener {
            finish()
        }

        loginText.setOnClickListener {
            finish()
        }

        registerButton.setOnClickListener {
            registerFarmer()
        }
    }

    private fun registerFarmer() {

        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (name.isEmpty()) {
            nameEditText.error = "Please enter your name"
            nameEditText.requestFocus()
            return
        }

        if (email.isEmpty()) {
            emailEditText.error = "Please enter your email"
            emailEditText.requestFocus()
            return
        }

        if (phone.length != 10) {
            phoneEditText.error = "Enter a valid 10-digit mobile number"
            phoneEditText.requestFocus()
            return
        }

        if (password.length < 6) {
            passwordEditText.error = "Password must contain at least 6 characters"
            passwordEditText.requestFocus()
            return
        }

        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            confirmPasswordEditText.requestFocus()
            return
        }

        setLoading(true)

        // Create Firebase Authentication account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (!task.isSuccessful) {

                    setLoading(false)

                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnCompleteListener
                }

                val user = auth.currentUser

                if (user == null) {
                    setLoading(false)
                    return@addOnCompleteListener
                }

                val farmerData = hashMapOf(
                    "uid" to user.uid,
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "createdAt" to System.currentTimeMillis()
                )

                // Store farmer profile in Firestore
                firestore.collection("farmers")
                    .document(user.uid)
                    .set(farmerData)
                    .addOnSuccessListener {

                        setLoading(false)

                        Toast.makeText(
                            this,
                            "Account created successfully! 🌱",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(this, MainActivity::class.java)
                        )

                        finishAffinity()
                    }
                    .addOnFailureListener { exception ->

                        setLoading(false)

                        Toast.makeText(
                            this,
                            "Account created, but profile could not be saved: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
    }

    private fun setLoading(isLoading: Boolean) {

        if (isLoading) {

            progressBar.visibility = View.VISIBLE
            registerButton.isEnabled = false
            registerButton.text = "CREATING ACCOUNT..."

        } else {

            progressBar.visibility = View.GONE
            registerButton.isEnabled = true
            registerButton.text = "CREATE ACCOUNT"
        }
    }
}