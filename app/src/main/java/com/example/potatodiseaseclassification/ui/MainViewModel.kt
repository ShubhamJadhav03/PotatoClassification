package com.example.potatodiseaseclassification.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potatodiseaseclassification.data.PotatoDiseaseService
import com.example.potatodiseaseclassification.data.PredictionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val service = PotatoDiseaseService()
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setImage(bitmap: Bitmap) {
        _uiState.value = UiState.ImageSelected(bitmap)
    }

    fun predictDisease() {
        val currentState = _uiState.value
        if (currentState !is UiState.ImageSelected) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = service.predictDisease(currentState.image)
                _uiState.value = UiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
} 