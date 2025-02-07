package com.example.Text_Recognizer.textrecognition

import android.content.Context
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ScanFace
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.InkFlow
import androidx.compose.ui.graphics.Color as ComposeColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraTextRecognitionScreen(
    documentSharedViewModel: DocumentSharedViewModel
) {
    HideSystemUI()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var detectedText by remember { mutableStateOf("No text detected yet.") }

    var currentCameraSelector by remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }


    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }


    LaunchedEffect(currentCameraSelector) {
        startCamera(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = previewView,
            cameraSelector = currentCameraSelector
        ) { newText ->
            detectedText = newText
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView }
        )


        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 0.dp)
                .height(110.dp)
                .background(
                    color = ComposeColor.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
                .shaderBackground(InkFlow)

                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedButton(
                    onClick = {

                        currentCameraSelector = if (currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                    }

                    

                    ,
                    modifier = Modifier.weight(1f)
                ) {

                    if (currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                        Icon(
                            imageVector = Lucide.ScanFace,
                            contentDescription = "Switch to front camera",
                            tint = ComposeColor.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Front Camera",
                            color = ComposeColor.White,
                            fontSize = 16.sp
                        )
                    } else {
                        Icon(
                            imageVector = Lucide.Camera,
                            contentDescription = "Switch to back camera",
                            tint = ComposeColor.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Back Camera",
                            color = ComposeColor.White,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(
                    onClick = {
                        documentSharedViewModel.appendRecognizedText(detectedText)
                        detectedText = ""
                    },
                    enabled = detectedText.isNotBlank(),

                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Append Text",
                        color = ComposeColor.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(106.dp)
                .background(ComposeColor.White.copy(alpha = 0.4f))
                .verticalScroll(scrollState)
        ) {
            Text(
                text = detectedText,
                modifier = Modifier.padding(16.dp),
                color = ComposeColor.Black
            )
        }
    }
}

private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraSelector: CameraSelector,
    onDetectedTextUpdated: (String) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder()
            .build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    TextRecognitionAnalyzer(onDetectedTextUpdated)
                )
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (exc: Exception) {
            Log.e("CameraTextRecognition", "Use case binding failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

@Composable
fun HideSystemUI() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        val originalSystemUiVisibility = view.systemUiVisibility
        view.systemUiVisibility = originalSystemUiVisibility or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        onDispose {
            view.systemUiVisibility = originalSystemUiVisibility
        }
    }
}
