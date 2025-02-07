package com.example.Text_Recognizer.textrecognition


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DocumentSharedViewModel : ViewModel() {

    private val _recognizedLines = MutableStateFlow<List<String>>(emptyList())
    val recognizedLines: StateFlow<List<String>> = _recognizedLines


    fun appendRecognizedText(text: String) {
        if (text.isNotBlank()) {
            viewModelScope.launch {
                _recognizedLines.value = _recognizedLines.value + text
            }
        }
    }


    fun clearRecognizedText() {
        viewModelScope.launch {
            _recognizedLines.value = emptyList()
        }
    }


    fun getFullText(): String = recognizedLines.value.joinToString("\n")
}
