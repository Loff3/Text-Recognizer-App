package com.example.Text_Recognizer.textrecognition


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {
    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme

    fun toggleTheme() {
        _darkTheme.value = !_darkTheme.value
    }

    fun setDarkTheme(value: Boolean) {
        _darkTheme.value = value
    }
}
