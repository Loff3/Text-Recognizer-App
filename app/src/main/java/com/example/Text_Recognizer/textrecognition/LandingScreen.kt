package com.example.Text_Recognizer.textrecognition

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.Text_Recognizer.R
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.InkFlow

val ZenDots = FontFamily(
    Font(R.font.zen_dots)
)


@SuppressLint("SuspiciousIndentation")
@Composable
fun LandingScreen(
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            // Shader
            .shaderBackground(InkFlow),

        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        // titel
        Text(
            text = "Text Recognizer",
            fontFamily = ZenDots,
            style = MaterialTheme.typography.displayLarge.copy(color = Color.White),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // undertext
        Text(
            text = "Let's Get Started Scanning!",
            fontFamily = ZenDots,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            textAlign = TextAlign.Center
        )

    }
}
