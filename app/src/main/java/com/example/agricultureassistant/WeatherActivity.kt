package com.example.agricultureassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherActivity : AppCompatActivity() {

    // UI elements
    private lateinit var locationText: TextView
    private lateinit var temperatureText: TextView
    private lateinit var weatherDescriptionText: TextView
    private lateinit var humidityText: TextView
    private lateinit var windText: TextView
    private lateinit var feelsLikeText: TextView
    private lateinit var adviceText: TextView
    private lateinit var weatherIcon: TextView
    private lateinit var progressBar: ProgressBar

    // Location service
    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    // Ask user for location permission
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineGranted || coarseGranted) {
                getCurrentLocation()
            } else {

                progressBar.visibility = ProgressBar.GONE

                locationText.text = "📍 Location permission denied"

                Toast.makeText(
                    this,
                    "Location permission is needed for local weather.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_weather)

        // Connect Kotlin variables with XML views
        locationText = findViewById(R.id.locationText)
        temperatureText = findViewById(R.id.temperatureText)
        weatherDescriptionText =
            findViewById(R.id.weatherDescriptionText)
        humidityText = findViewById(R.id.humidityText)
        windText = findViewById(R.id.windText)
        feelsLikeText = findViewById(R.id.feelsLikeText)
        adviceText = findViewById(R.id.adviceText)
        weatherIcon = findViewById(R.id.weatherIcon)
        progressBar = findViewById(R.id.weatherProgressBar)

        // Back button
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Check location permission
        checkLocationPermission()
    }

    // Check whether location permission already exists
    private fun checkLocationPermission() {

        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            getCurrentLocation()
        } else {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Get current device location
    private fun getCurrentLocation() {

        progressBar.visibility = ProgressBar.VISIBLE
        locationText.text = "📍 Finding your location..."

        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            return
        }

        locationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    locationText.text =
                        "📍 Local weather for your location"

                    // Send latitude and longitude to OpenWeather
                    loadWeather(
                        location.latitude,
                        location.longitude
                    )

                } else {

                    progressBar.visibility = ProgressBar.GONE

                    locationText.text =
                        "📍 Location unavailable"

                    Toast.makeText(
                        this,
                        "Unable to get device location. Please enable Location.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener {

                progressBar.visibility = ProgressBar.GONE

                locationText.text =
                    "📍 Unable to access location"

                Toast.makeText(
                    this,
                    "Could not access your location.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // Call OpenWeather API
    private fun loadWeather(
        latitude: Double,
        longitude: Double
    ) {

        val apiKey = BuildConfig.OPENWEATHER_API_KEY

        // Make sure API key exists
        if (apiKey.isBlank() || apiKey == "null") {

            progressBar.visibility = ProgressBar.GONE

            Toast.makeText(
                this,
                "OpenWeather API key is missing.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        RetrofitClient.weatherApi
            .getCurrentWeather(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey
            )
            .enqueue(object : Callback<WeatherResponse> {

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {

                    progressBar.visibility = ProgressBar.GONE

                    if (response.isSuccessful && response.body() != null) {

                        val weather = response.body()!!

                        displayWeather(weather)

                    } else {

                        weatherDescriptionText.text =
                            "Unable to load weather"

                        locationText.text =
                            "Weather service returned an error."

                        Toast.makeText(
                            this@WeatherActivity,
                            "Weather API error: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<WeatherResponse>,
                    t: Throwable
                ) {

                    progressBar.visibility = ProgressBar.GONE

                    weatherDescriptionText.text =
                        "Unable to load weather"

                    locationText.text =
                        "Please check your internet connection."

                    Toast.makeText(
                        this@WeatherActivity,
                        "Weather service unavailable.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // Display real weather data on the screen
    private fun displayWeather(weather: WeatherResponse) {

        val mainWeather = weather.main
        val weatherInfo = weather.weather?.firstOrNull()
        val windInfo = weather.wind

        // Temperature
        val temperature = mainWeather?.temp ?: 0.0

        // Feels like
        val feelsLike = mainWeather?.feels_like ?: 0.0

        // Humidity
        val humidity = mainWeather?.humidity ?: 0

        // OpenWeather gives wind speed in m/s.
        // Convert it to km/h.
        val windSpeedMs = windInfo?.speed ?: 0.0
        val windSpeedKmh = windSpeedMs * 3.6

        // Weather description
        val description =
            weatherInfo?.description
                ?.replaceFirstChar { it.uppercase() }
                ?: "Weather unavailable"

        // Weather type
        val weatherMain =
            weatherInfo?.main ?: ""

        // Weather icon
        val icon = getWeatherIcon(weatherMain)

        // Farming advice
        val advice = getFarmingAdvice(
            weatherMain,
            temperature
        )

        // Update UI
        temperatureText.text =
            "${temperature.toInt()}°C"

        feelsLikeText.text =
            "${feelsLike.toInt()}°C"

        humidityText.text =
            "$humidity%"

        windText.text =
            "${windSpeedKmh.toInt()} km/h"

        weatherDescriptionText.text =
            description

        weatherIcon.text =
            icon

        adviceText.text =
            advice

        // Show city returned by OpenWeather
        if (!weather.name.isNullOrBlank()) {
            locationText.text =
                "📍 ${weather.name}"
        } else {
            locationText.text =
                "📍 Local weather for your location"
        }
    }

    // Select weather icon
    private fun getWeatherIcon(
        weatherMain: String
    ): String {

        return when (weatherMain.lowercase()) {

            "clear" -> "☀️"

            "clouds" -> "☁️"

            "rain" -> "🌧️"

            "drizzle" -> "🌦️"

            "thunderstorm" -> "⛈️"

            "snow" -> "🌨️"

            "mist",
            "smoke",
            "haze",
            "dust",
            "fog",
            "sand",
            "ash",
            "squall",
            "tornado" -> "🌫️"

            else -> "🌤️"
        }
    }

    // Generate simple farming advice
    private fun getFarmingAdvice(
        weatherMain: String,
        temperature: Double
    ): String {

        return when {

            weatherMain.equals(
                "Rain",
                ignoreCase = true
            ) ||
                    weatherMain.equals(
                        "Drizzle",
                        ignoreCase = true
                    ) -> {

                "🌧️ Rainy conditions detected. Avoid unnecessary irrigation and protect harvested crops from excess moisture."
            }

            weatherMain.equals(
                "Thunderstorm",
                ignoreCase = true
            ) -> {

                "⛈️ Thunderstorm conditions detected. Avoid working in open fields during lightning or strong storms."
            }

            temperature >= 35 -> {

                "☀️ High temperature detected. Consider irrigation during cooler hours and protect young crops from heat."
            }

            temperature <= 10 -> {

                "❄️ Cool conditions detected. Monitor sensitive crops and protect plants from possible cold stress."
            }

            else -> {

                "🌾 Weather conditions look suitable for routine field activities. Continue monitoring the forecast."
            }
        }
    }
}