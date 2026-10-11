package com.example.agricultureassistant

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var nameText: TextView
    private lateinit var emailText: TextView
    private lateinit var initialText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        nameText = findViewById(R.id.nameText)
        emailText = findViewById(R.id.emailText)
        initialText = findViewById(R.id.initialText)

        findViewById<TextView>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.logoutButton).setOnClickListener {
            logoutFarmer()
        }

        findViewById<MaterialButton>(R.id.deleteAccountButton).setOnClickListener {
            showDeleteConfirmation()
        }

        loadProfile()
    }

    private fun loadProfile() {

        val user = auth.currentUser

        if (user == null) {
            goToLogin()
            return
        }

        emailText.text = user.email ?: "No email available"

        firestore.collection("farmers")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val name = document.getString("name") ?: "Farmer"

                    nameText.text = name
                    initialText.text =
                        name.firstOrNull()?.uppercaseChar()?.toString() ?: "F"

                } else {

                    nameText.text = "Farmer"
                    initialText.text = "F"
                }
            }
            .addOnFailureListener {

                nameText.text = "Farmer"
                initialText.text = "F"
            }
    }

    private fun logoutFarmer() {

        auth.signOut()

        Toast.makeText(
            this,
            "Logged out successfully",
            Toast.LENGTH_SHORT
        ).show()

        goToLogin()
    }

    private fun showDeleteConfirmation() {

        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage(
                "Are you sure you want to delete your account? " +
                        "This action cannot be undone."
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                deleteAccount()
            }
            .show()
    }

    private fun deleteAccount() {

        val user = auth.currentUser

        if (user == null) {
            goToLogin()
            return
        }

        val uid = user.uid

        firestore.collection("farmers")
            .document(uid)
            .delete()
            .addOnSuccessListener {

                user.delete()
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Account deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        goToLogin()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Unable to delete account. Please login again and try.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to delete profile data.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun goToLogin() {

        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}