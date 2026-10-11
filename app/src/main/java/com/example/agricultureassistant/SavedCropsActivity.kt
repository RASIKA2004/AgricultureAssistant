package com.example.agricultureassistant

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SavedCropsActivity : AppCompatActivity() {

    private lateinit var databaseHelper: CropDatabaseHelper
    private lateinit var cropContainer: LinearLayout
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_saved_crops)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        databaseHelper = CropDatabaseHelper(this)

        cropContainer = findViewById(R.id.cropContainer)
        emptyText = findViewById(R.id.emptyText)

        findViewById<TextView>(R.id.backButton).setOnClickListener {
            finish()
        }

        loadSavedCrops()
    }

    override fun onResume() {
        super.onResume()

        if (::databaseHelper.isInitialized) {
            loadSavedCrops()
        }
    }

    private fun loadSavedCrops() {

        cropContainer.removeAllViews()

        val crops = databaseHelper.getAllCrops()

        if (crops.isEmpty()) {

            emptyText.visibility = TextView.VISIBLE

        } else {

            emptyText.visibility = TextView.GONE

            for (crop in crops) {
                addCropCard(crop)
            }
        }
    }

    private fun addCropCard(crop: Crop) {

        val card = LinearLayout(this)

        card.orientation = LinearLayout.VERTICAL
        card.setPadding(20, 20, 20, 20)

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        cardParams.setMargins(0, 0, 0, 16)

        card.layoutParams = cardParams

        card.setBackgroundResource(
            android.R.drawable.dialog_holo_light_frame
        )

        // Crop Name
        val cropName = TextView(this)

        cropName.text = "🌱 ${crop.cropName}"
        cropName.textSize = 20f
        cropName.setTextColor(Color.rgb(35, 110, 60))
        cropName.setPadding(0, 0, 0, 10)

        card.addView(cropName)

        // Sowing Date
        val sowingDate = TextView(this)

        sowingDate.text = "📅 Sowing Date: ${crop.sowingDate}"
        sowingDate.textSize = 15f

        card.addView(sowingDate)

        // Area
        val area = TextView(this)

        area.text = "📐 Field Area: ${crop.area}"
        area.textSize = 15f

        card.addView(area)

        // Status
        val status = TextView(this)

        status.text = "🌾 Status: ${crop.status}"
        status.textSize = 15f

        card.addView(status)

        // Notes
        val notes = TextView(this)

        notes.text = "📝 Notes: ${crop.notes.ifEmpty { "No notes" }}"
        notes.textSize = 15f
        notes.setPadding(0, 8, 0, 12)

        card.addView(notes)

        // Buttons Layout
        val buttonLayout = LinearLayout(this)

        buttonLayout.orientation = LinearLayout.HORIZONTAL
        buttonLayout.gravity = Gravity.CENTER

        // Edit Button
        val editButton = Button(this)

        editButton.text = "EDIT CROP"
        editButton.setOnClickListener {
            showEditDialog(crop)
        }

        val editParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        )

        editParams.setMargins(0, 0, 8, 0)

        editButton.layoutParams = editParams

        buttonLayout.addView(editButton)

        // Delete Button
        val deleteButton = Button(this)

        deleteButton.text = "DELETE CROP"

        deleteButton.setTextColor(Color.RED)

        deleteButton.setOnClickListener {
            showDeleteConfirmation(crop)
        }

        val deleteParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        )

        deleteParams.setMargins(8, 0, 0, 0)

        deleteButton.layoutParams = deleteParams

        buttonLayout.addView(deleteButton)

        card.addView(buttonLayout)

        cropContainer.addView(card)
    }

    // ---------------- EDIT CROP ----------------

    private fun showEditDialog(crop: Crop) {

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 10, 30, 10)

        // Crop Name
        val cropNameInput = EditText(this)
        cropNameInput.hint = "Crop Name"
        cropNameInput.setText(crop.cropName)

        layout.addView(cropNameInput)

        // Sowing Date
        val sowingDateInput = EditText(this)
        sowingDateInput.hint = "Sowing Date"
        sowingDateInput.setText(crop.sowingDate)

        layout.addView(sowingDateInput)

        // Area
        val areaInput = EditText(this)
        areaInput.hint = "Field Area"
        areaInput.setText(crop.area)

        layout.addView(areaInput)

        // Status
        val statusInput = EditText(this)
        statusInput.hint = "Crop Status"
        statusInput.setText(crop.status)

        layout.addView(statusInput)

        // Notes
        val notesInput = EditText(this)
        notesInput.hint = "Notes"
        notesInput.setText(crop.notes)
        notesInput.minLines = 3
        notesInput.gravity = Gravity.TOP

        layout.addView(notesInput)

        AlertDialog.Builder(this)
            .setTitle("Edit Crop")
            .setView(layout)
            .setPositiveButton("SAVE") { _, _ ->

                val updatedCrop = Crop(
                    id = crop.id,
                    cropName = cropNameInput.text.toString().trim(),
                    sowingDate = sowingDateInput.text.toString().trim(),
                    area = areaInput.text.toString().trim(),
                    status = statusInput.text.toString().trim(),
                    notes = notesInput.text.toString().trim()
                )

                if (
                    updatedCrop.cropName.isEmpty() ||
                    updatedCrop.sowingDate.isEmpty() ||
                    updatedCrop.area.isEmpty() ||
                    updatedCrop.status.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Please fill all required fields",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val result =
                    databaseHelper.updateCrop(updatedCrop)

                if (result > 0) {

                    Toast.makeText(
                        this,
                        "Crop updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadSavedCrops()

                } else {

                    Toast.makeText(
                        this,
                        "Unable to update crop",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    // ---------------- DELETE CROP ----------------

    private fun showDeleteConfirmation(crop: Crop) {

        AlertDialog.Builder(this)
            .setTitle("Delete Crop")
            .setMessage(
                "Are you sure you want to delete \"${crop.cropName}\"?"
            )
            .setPositiveButton("Delete") { _, _ ->

                val result =
                    databaseHelper.deleteCrop(crop.id)

                if (result > 0) {

                    Toast.makeText(
                        this,
                        "Crop deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadSavedCrops()

                } else {

                    Toast.makeText(
                        this,
                        "Unable to delete crop",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}