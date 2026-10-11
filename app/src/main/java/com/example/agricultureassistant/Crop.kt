package com.example.agricultureassistant

data class Crop(
    val id: Int = 0,
    val cropName: String,
    val sowingDate: String,
    val area: String,
    val status: String,
    val notes: String
)