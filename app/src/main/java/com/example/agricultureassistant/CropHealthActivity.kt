package com.example.agricultureassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import com.example.agricultureassistant.ml.CropHealthML


class CropHealthActivity : AppCompatActivity() {

    private lateinit var cropImageView: ImageView
    private lateinit var captureButton: Button
    private lateinit var analyzeButton: Button
    private lateinit var statusText: TextView
    private lateinit var resultText: TextView
    private lateinit var adviceText: TextView

    private lateinit var cropHealthML: CropHealthML


    private var photoUri: Uri? = null

    private val CAMERA_PERMISSION_REQUEST = 101
    private val CAMERA_CAPTURE_REQUEST = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_crop_health)


        cropHealthML = CropHealthML(this)
        cropImageView = findViewById(R.id.cropImageView)
        captureButton = findViewById(R.id.captureButton)
        analyzeButton = findViewById(R.id.analyzeButton)
        statusText = findViewById(R.id.statusText)
        resultText = findViewById(R.id.resultText)
        adviceText = findViewById(R.id.adviceText)

        findViewById<TextView>(R.id.backButton).setOnClickListener {
            finish()
        }

        captureButton.setOnClickListener {
            checkCameraPermission()
        }

        analyzeButton.setOnClickListener {
            analyzeCropImage()
        }
    }

    private fun checkCameraPermission() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        }
    }

    private fun openCamera() {

        val photoFile = createImageFile()

        photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        intent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            photoUri
        )

        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivityForResult(
            intent,
            CAMERA_CAPTURE_REQUEST
        )
    }

    private fun createImageFile(): File {

        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())

        val storageDir = getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )

        return File.createTempFile(
            "CROP_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == CAMERA_CAPTURE_REQUEST) {

            if (resultCode == RESULT_OK) {

                photoUri?.let { uri ->

                    cropImageView.setImageURI(uri)

                    statusText.text =
                        "Crop image captured and stored successfully"

                    analyzeButton.isEnabled = true

                    Toast.makeText(
                        this,
                        "Crop photo saved successfully 📷",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {

                photoUri?.path?.let {
                    File(it).delete()
                }

                statusText.text = "Photo capture cancelled"
                analyzeButton.isEnabled = false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == CAMERA_PERMISSION_REQUEST) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun analyzeCropImage() {

        val uri = photoUri

        if (uri == null) {
            resultText.text = "❌ No image available"
            adviceText.text =
                "Please capture a crop image first."
            return
        }

        try {

            val inputStream =
                contentResolver.openInputStream(uri)

            val bitmap =
                BitmapFactory.decodeStream(inputStream)

            inputStream?.close()

            if (bitmap == null) {
                resultText.text = "❌ Unable to read image"
                adviceText.text =
                    "Please capture the image again."
                return
            }

            runMLAnalysis(bitmap)


        } catch (e: Exception) {

            resultText.text = "❌ Analysis failed"
            adviceText.text =
                "Unable to process the captured image."
        }
    }

    private fun runMLAnalysis(bitmap: Bitmap) {

        try {

            val prediction = cropHealthML.predict(bitmap)

            val health = prediction.health
            val disease = prediction.disease

            resultText.text =
                "🌱 Health: ${health.status}\n" +
                        "Confidence: ${"%.2f".format(health.confidence * 100)}%\n\n" +
                        "🦠 Disease: ${disease.disease}\n" +
                        "Confidence: ${"%.2f".format(disease.confidence * 100)}%"

            val top3Text = disease.top3.joinToString("\n") { item ->
                "${item.first}: ${"%.2f".format(item.second * 100)}%"
            }

            adviceText.text =
                "Top 3 Predictions:\n\n$top3Text"

            statusText.text =
                "AI analysis completed successfully"

        } catch (e: Exception) {

            resultText.text = "❌ AI analysis failed"

            adviceText.text =
                "Unable to analyze the crop image. " +
                        "Please try again with a clear image."

            statusText.text =
                "Analysis failed"

            Toast.makeText(
                this,
                "AI analysis failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}