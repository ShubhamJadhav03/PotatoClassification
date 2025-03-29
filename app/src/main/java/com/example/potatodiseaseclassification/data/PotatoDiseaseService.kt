package com.example.potatodiseaseclassification.data

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class PotatoDiseaseService {
    private val client = OkHttpClient()
    private val baseUrl = "https://us-central1-soy-truth-455012-r5.cloudfunctions.net/predict"

    suspend fun predictDisease(image: Bitmap): PredictionResponse = withContext(Dispatchers.IO) {
        // Convert bitmap to byte array
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val byteArray = stream.toByteArray()

        // Create multipart request body
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",  // The parameter name should match what the server expects
                "image.jpg",
                byteArray.toRequestBody("image/jpeg".toMediaType())
            )
            .build()

        // Create request
        val request = Request.Builder()
            .url(baseUrl)
            .post(requestBody)
            .build()

        // Execute request
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw Exception("Empty response")
        
        if (!response.isSuccessful) {
            throw Exception("API call failed with code: ${response.code}. Response: $responseBody")
        }

        try {
            val jsonResponse = JSONObject(responseBody)
            PredictionResponse(
                prediction = jsonResponse.getString("class"),
                confidence = jsonResponse.getDouble("confidence")
            )
        } catch (e: Exception) {
            throw Exception("Failed to parse response: $responseBody. Error: ${e.message}")
        }
    }
} 