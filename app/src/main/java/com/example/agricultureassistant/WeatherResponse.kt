package com.example.agricultureassistant

data class WeatherResponse(
    val name: String?,
    val main: MainWeather?,
    val weather: List<WeatherInfo>?,
    val wind: WindInfo?
)

data class MainWeather(
    val temp: Double?,
    val feels_like: Double?,
    val humidity: Int?
)

data class WeatherInfo(
    val main: String?,
    val description: String?
)

data class WindInfo(
    val speed: Double?
)