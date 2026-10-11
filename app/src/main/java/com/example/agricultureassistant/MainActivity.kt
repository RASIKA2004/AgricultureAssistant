package com.example.agricultureassistant

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var farmerNameText: TextView
    private lateinit var profileInitialText: TextView
    private lateinit var logoutButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Dashboard UI
        farmerNameText = findViewById(R.id.farmerNameText)
        profileInitialText = findViewById(R.id.profileInitialText)
        logoutButton = findViewById(R.id.logoutButton)

        // Load farmer information
        loadFarmerProfile()

        // Logout
        logoutButton.setOnClickListener {
            logoutFarmer()
        }

        // Weather
        findViewById<View>(R.id.weatherCard).setOnClickListener {

            startActivity(
                Intent(this, WeatherActivity::class.java)
            )
        }

        // Market Finder
        findViewById<View>(R.id.marketCard).setOnClickListener {

            startActivity(
                Intent(this, MarketFinderActivity::class.java)
            )
        }

        // Crop Diary
        findViewById<View>(R.id.cropDiaryCard).setOnClickListener {

            startActivity(
                Intent(this, CropDiaryActivity::class.java)
            )
        }

        // Crop Health
        findViewById<View>(R.id.cropHealthCard).setOnClickListener {
            startActivity(
                Intent(this, CropHealthActivity::class.java)
            )
        }

        // Farming Tips
        findViewById<View>(R.id.tipsCard).setOnClickListener {
            startActivity(
                Intent(this, FarmingTipsActivity::class.java)
            )
        }

        // Profile
        findViewById<View>(R.id.profileInitialText).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<View>(R.id.farmerNameText).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<View>(R.id.profileCard).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun loadFarmerProfile() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            goToLogin()
            return
        }

        firestore.collection("farmers")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val name = document.getString("name")

                    if (!name.isNullOrEmpty()) {

                        farmerNameText.text = name

                        profileInitialText.text =
                            name.first().uppercaseChar().toString()

                    } else {

                        farmerNameText.text = "Farmer"
                        profileInitialText.text = "F"
                    }

                } else {

                    farmerNameText.text = "Farmer"
                    profileInitialText.text = "F"
                }
            }
            .addOnFailureListener {

                farmerNameText.text = "Farmer"
                profileInitialText.text = "F"
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