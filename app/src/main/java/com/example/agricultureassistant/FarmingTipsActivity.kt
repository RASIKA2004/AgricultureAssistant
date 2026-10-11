package com.example.agricultureassistant

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FarmingTipsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_farming_tips)

        // Back button
        findViewById<TextView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Crop Care
        findViewById<View>(R.id.cropCareCard).setOnClickListener {
            openTips("Crop Care")
        }

        // Irrigation
        findViewById<View>(R.id.irrigationCard).setOnClickListener {
            openTips("Irrigation")
        }

        // Pest Management
        findViewById<View>(R.id.pestManagementCard).setOnClickListener {
            openTips("Pest Management")
        }

        // Fertilizer
        findViewById<View>(R.id.fertilizerCard).setOnClickListener {
            openTips("Fertilizer")
        }

        // Weather
        findViewById<View>(R.id.weatherTipsCard).setOnClickListener {
            openTips("Weather")
        }

        findViewById<View>(R.id.audioGuideCard).setOnClickListener {
            startActivity(Intent(this, MusicActivity::class.java))
        }
    }

    private fun openTips(category: String) {

        val intent = Intent(
            this,
            TipsListActivity::class.java
        )

        intent.putExtra("CATEGORY", category)

        startActivity(intent)
    }
}