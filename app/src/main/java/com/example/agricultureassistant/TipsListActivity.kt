package com.example.agricultureassistant

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TipsListActivity : AppCompatActivity() {

    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tips_list)

        category = intent.getStringExtra("CATEGORY") ?: "Farming Tips"

        val titleText = findViewById<TextView>(R.id.titleText)
        val tipsText = findViewById<TextView>(R.id.tipsText)
        val backButton = findViewById<TextView>(R.id.backButton)

        titleText.text = category

        tipsText.text = getTips(category)

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun getTips(category: String): String {

        return when (category) {

            "Crop Care" -> """
                🌾 Crop Care Tips

                • Use healthy and good-quality seeds.

                • Keep the field free from excessive weeds.

                • Monitor crops regularly for changes in growth or appearance.

                • Maintain proper spacing between plants according to the crop.

                • Remove damaged plant parts when necessary.

                • Keep records of important crop activities in your Crop Diary.
            """.trimIndent()

            "Irrigation" -> """
                💧 Irrigation Tips

                • Water crops according to their growth stage and soil condition.

                • Avoid unnecessary over-irrigation.

                • Check soil moisture before watering.

                • Prefer efficient irrigation methods where suitable.

                • Provide proper drainage to prevent waterlogging.

                • Adjust irrigation according to weather conditions.
            """.trimIndent()

            "Pest Management" -> """
                🐛 Pest Management Tips

                • Inspect plants regularly for signs of pests.

                • Check the underside of leaves for insects or damage.

                • Keep the field clean and remove heavily affected plant material.

                • Encourage healthy crop growth through proper field management.

                • Identify the pest correctly before choosing a control method.

                • Follow the recommended instructions when using any pesticide.
            """.trimIndent()

            "Fertilizer" -> """
                🌿 Fertilizer Tips

                • Understand the nutrient requirements of your crop.

                • Prefer soil testing when available.

                • Apply fertilizers according to recommended quantities.

                • Avoid excessive fertilizer application.

                • Apply nutrients at appropriate crop growth stages.

                • Maintain proper records of fertilizer applications.
            """.trimIndent()

            "Weather" -> """
                ☀️ Weather Tips

                • Check weather conditions before important farm activities.

                • Plan irrigation according to expected rainfall.

                • Avoid unnecessary spraying during unsuitable weather.

                • Protect crops when extreme weather is expected.

                • Monitor temperature and rainfall conditions regularly.

                • Use the Weather feature in Agriculture Assistant for current conditions.
            """.trimIndent()

            else -> """
                🌱 Farming Tips

                • Monitor your crops regularly.

                • Keep proper farming records.

                • Plan farm activities according to crop requirements.

                • Pay attention to soil, water and weather conditions.
            """.trimIndent()
        }
    }
}