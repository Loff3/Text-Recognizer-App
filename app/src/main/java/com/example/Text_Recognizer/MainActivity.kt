package com.example.Text_Recognizer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.Text_Recognizer.textrecognition.PermissionHandler
import com.example.Text_Recognizer.textrecognition.ThemeViewModel
import com.example.Text_Recognizer.ui.theme.TextTheme
import kotlinx.coroutines.delay


class MainActivity : PermissionHandler() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
       // splashScreen
        setContent {
            val themeViewModel: ThemeViewModel by viewModels()

            val isDarkMode by themeViewModel.darkTheme.collectAsState()

            TextTheme(darkTheme = isDarkMode) {
                val permissionGranted = isCameraPermissionGranted.collectAsState().value
                PermissionScreen(
                    permissionGranted = permissionGranted,
                    onDecline = { finish() },
                    onAccept = { handleCameraPermission() },
                    themeViewModel = themeViewModel
                )
            }
        }
    }


    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun PermissionScreen(
        permissionGranted: Boolean,
        onDecline: () -> Unit,
        onAccept: () -> Unit,
        themeViewModel: ThemeViewModel

    ) {
        if (permissionGranted) {
            MyApp(themeViewModel)

        } else {
            var isVisible by remember { mutableStateOf(true) }
            var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(24.dp)
                            .width(300.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Camera Permission",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "In order for this application to run you need to grant permission for the use of the camera.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = {
                                    isVisible = false
                                    pendingAction = { onDecline() }
                                }) {
                                    Text("Decline")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = {
                                    isVisible = false
                                    pendingAction = { onAccept() }
                                }) {
                                    Text("Accept")
                                }
                            }
                        }
                    }
                }
            }

            if (!isVisible && pendingAction != null) {
                LaunchedEffect(pendingAction) {
                    delay(100)
                    pendingAction?.invoke()
                    pendingAction = null
                }
            }
        }
    }
}