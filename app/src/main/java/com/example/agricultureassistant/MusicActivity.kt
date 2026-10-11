package com.example.agricultureassistant

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MusicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_music)

        val backButton = findViewById<TextView>(R.id.backButton)
        val hindiButton = findViewById<Button>(R.id.hindiButton)
        val englishButton = findViewById<Button>(R.id.englishButton)
        val stopButton = findViewById<Button>(R.id.stopButton)

        backButton.setOnClickListener {
            finish()
        }

        hindiButton.setOnClickListener {
            val intent = Intent(this, MusicService::class.java)
            intent.putExtra("LANGUAGE", "hindi")
            startService(intent)
        }

        englishButton.setOnClickListener {
            val intent = Intent(this, MusicService::class.java)
            intent.putExtra("LANGUAGE", "english")
            startService(intent)
        }

        stopButton.setOnClickListener {
            stopService(Intent(this, MusicService::class.java))
        }
    }
}