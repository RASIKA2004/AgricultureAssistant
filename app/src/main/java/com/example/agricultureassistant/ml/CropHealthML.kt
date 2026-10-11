package com.example.agricultureassistant.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class CropHealthML(private val context: Context) {

    companion object {
        private const val IMAGE_SIZE = 224

        private const val HEALTH_MODEL =
            "Agriculture_Assistant_Healthy_vs_Stressed_MobileNetV2.tflite"

        private const val DISEASE_MODEL =
            "Agriculture_Assistant_Disease_MobileNetV2.tflite"

        private val DISEASE_CLASSES = arrayOf(
            "Pepper__bell___Bacterial_spot",
            "Pepper__bell___healthy",
            "Potato___Early_blight",
            "Potato___Late_blight",
            "Potato___healthy",
            "Tomato_Bacterial_spot",
            "Tomato_Early_blight",
            "Tomato_Late_blight",
            "Tomato_Leaf_Mold",
            "Tomato_Septoria_leaf_spot",
            "Tomato_Spider_mites_Two_spotted_spider_mite",
            "Tomato__Target_Spot",
            "Tomato__Tomato_YellowLeaf__Curl_Virus",
            "Tomato__Tomato_mosaic_virus",
            "Tomato_healthy"
        )
    }

    private val healthInterpreter: Interpreter
    private val diseaseInterpreter: Interpreter

    init {
        healthInterpreter = Interpreter(loadModelFile(HEALTH_MODEL))
        diseaseInterpreter = Interpreter(loadModelFile(DISEASE_MODEL))
    }

    data class HealthResult(
        val status: String,
        val confidence: Float
    )

    data class DiseaseResult(
        val disease: String,
        val confidence: Float,
        val top3: List<Pair<String, Float>>
    )

    data class PredictionResult(
        val health: HealthResult,
        val disease: DiseaseResult
    )

    private fun loadModelFile(modelName: String): ByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)

        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel

        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            startOffset,
            declaredLength
        )
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {

        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            IMAGE_SIZE,
            IMAGE_SIZE,
            true
        )

        val inputBuffer = ByteBuffer.allocateDirect(
            4 * IMAGE_SIZE * IMAGE_SIZE * 3
        )

        inputBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)

        resizedBitmap.getPixels(
            pixels,
            0,
            IMAGE_SIZE,
            0,
            0,
            IMAGE_SIZE,
            IMAGE_SIZE
        )

        for (pixel in pixels) {

            val red = (pixel shr 16) and 0xFF
            val green = (pixel shr 8) and 0xFF
            val blue = pixel and 0xFF

            inputBuffer.putFloat(red / 127.5f - 1.0f)
            inputBuffer.putFloat(green / 127.5f - 1.0f)
            inputBuffer.putFloat(blue / 127.5f - 1.0f)
        }

        inputBuffer.rewind()

        return inputBuffer
    }

    fun predict(bitmap: Bitmap): PredictionResult {

        val inputBuffer = preprocessImage(bitmap)

        // -----------------------------
        // HEALTH MODEL
        // -----------------------------

        val healthOutput = Array(1) { FloatArray(1) }

        healthInterpreter.run(
            inputBuffer,
            healthOutput
        )

        val healthRaw = healthOutput[0][0]

        val healthStatus: String
        val healthConfidence: Float

        if (healthRaw >= 0.5f) {
            healthStatus = "Stressed"
            healthConfidence = healthRaw
        } else {
            healthStatus = "Healthy"
            healthConfidence = 1.0f - healthRaw
        }

        // -----------------------------
        // DISEASE MODEL
        // -----------------------------

        val diseaseOutput = Array(1) { FloatArray(15) }

        diseaseInterpreter.run(
            inputBuffer,
            diseaseOutput
        )

        val probabilities = diseaseOutput[0]

        val sortedIndices = probabilities.indices
            .sortedByDescending { probabilities[it] }

        val top3 = sortedIndices
            .take(3)
            .map { index ->
                Pair(
                    DISEASE_CLASSES[index],
                    probabilities[index]
                )
            }

        val bestIndex = sortedIndices.first()

        val diseaseResult = DiseaseResult(
            disease = DISEASE_CLASSES[bestIndex],
            confidence = probabilities[bestIndex],
            top3 = top3
        )

        return PredictionResult(
            health = HealthResult(
                status = healthStatus,
                confidence = healthConfidence
            ),
            disease = diseaseResult
        )
    }

    fun close() {
        healthInterpreter.close()
        diseaseInterpreter.close()
    }
}