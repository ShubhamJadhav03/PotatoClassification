package com.example.potatodiseaseclassification.ui

import android.graphics.Bitmap
import com.example.potatodiseaseclassification.data.PredictionResponse

sealed interface UiState {
    data object Initial : UiState
    data class ImageSelected(val image: Bitmap) : UiState
    data object Loading : UiState
    data class Success(val result: PredictionResponse) : UiState
    data class Error(val message: String) : UiState
} 