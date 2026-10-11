package com.example.agricultureassistant

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val language = intent?.getStringExtra("LANGUAGE") ?: "english"

        mediaPlayer?.release()

        val audioResource = if (language == "hindi") {
            R.raw.agriculture_assistant_hindi
        } else {
            R.raw.agriculture_assistant_english
        }

        mediaPlayer = MediaPlayer.create(this, audioResource)

        mediaPlayer?.start()

        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}