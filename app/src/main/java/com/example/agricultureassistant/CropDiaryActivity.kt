package com.example.agricultureassistant

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CropDiaryActivity : AppCompatActivity() {

    private lateinit var etCropName: EditText
    private lateinit var etSowingDate: EditText
    private lateinit var etArea: EditText
    private lateinit var etNotes: EditText
    private lateinit var spinnerStatus: Spinner

    private lateinit var btnSaveCrop: Button
    private lateinit var btnViewCrops: Button

    private lateinit var databaseHelper: CropDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_crop_diary)

        // Initialize database
        databaseHelper = CropDatabaseHelper(this)

        // Connect UI elements
        etCropName = findViewById(R.id.etCropName)
        etSowingDate = findViewById(R.id.etSowingDate)
        etArea = findViewById(R.id.etArea)
        etNotes = findViewById(R.id.etNotes)

        spinnerStatus = findViewById(R.id.spinnerStatus)

        btnSaveCrop = findViewById(R.id.btnSaveCrop)
        btnViewCrops = findViewById(R.id.btnViewCrops)

        // Crop status options
        val statusOptions = arrayOf(
            "Growing",
            "Recently Sown",
            "Flowering",
            "Ready for Harvest",
            "Harvested"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            statusOptions
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerStatus.adapter = adapter

        // Save crop
        btnSaveCrop.setOnClickListener {
            saveCrop()
        }

        // View saved crops
        btnViewCrops.setOnClickListener {
            viewSavedCrops()
        }
    }

    private fun saveCrop() {

        val cropName = etCropName.text.toString().trim()
        val sowingDate = etSowingDate.text.toString().trim()
        val area = etArea.text.toString().trim()
        val status = spinnerStatus.selectedItem.toString()
        val notes = etNotes.text.toString().trim()

        // Validate required fields
        if (cropName.isEmpty()) {
            etCropName.error = "Please enter crop name"
            etCropName.requestFocus()
            return
        }

        if (sowingDate.isEmpty()) {
            etSowingDate.error = "Please enter sowing date"
            etSowingDate.requestFocus()
            return
        }

        if (area.isEmpty()) {
            etArea.error = "Please enter field area"
            etArea.requestFocus()
            return
        }

        // Create Crop object
        val crop = Crop(
            cropName = cropName,
            sowingDate = sowingDate,
            area = area,
            status = status,
            notes = notes
        )

        // Insert into SQLite
        val result = databaseHelper.addCrop(crop)

        if (result != -1L) {

            Toast.makeText(
                this,
                "Crop saved successfully 🌱",
                Toast.LENGTH_SHORT
            ).show()

            clearFields()

        } else {

            Toast.makeText(
                this,
                "Failed to save crop",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun clearFields() {

        etCropName.text.clear()
        etSowingDate.text.clear()
        etArea.text.clear()
        etNotes.text.clear()

        spinnerStatus.setSelection(0)
    }

    private fun viewSavedCrops() {

        val crops = databaseHelper.getAllCrops()

        if (crops.isEmpty()) {

            Toast.makeText(
                this,
                "No crop records found",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            // Open Saved Crops screen
            val intent = Intent(
                this,
                SavedCropsActivity::class.java
            )

            startActivity(intent)
        }
    }
}