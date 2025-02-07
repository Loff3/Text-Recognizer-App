package com.example.Text_Recognizer.textrecognition

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.InkFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun DocumentEditingScreen(
    documentViewModel: DocumentSharedViewModel
) {
    var title by rememberSaveable { mutableStateOf("Untitled") }
    var saveLocally by rememberSaveable { mutableStateOf(true) }
    var saveToCloud by rememberSaveable { mutableStateOf(false) }
    val fullText by documentViewModel.recognizedLines
        .map { it.joinToString("\n") }
        .collectAsState(initial = "No text available.")
    var editableText by rememberSaveable { mutableStateOf(fullText) }

    var pdfFile by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(fullText) {
        editableText = fullText
    }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogText by remember { mutableStateOf("") }

    val context = LocalContext.current

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
        onResult = { uri: Uri? ->
            if (uri == null) {
                dialogTitle = "Failed"
                dialogText = "No location was chosen to store the PDF."
                showDialog = true
            } else {
                pdfFile?.let { safePdfFile ->
                    copyFileToUri(context, safePdfFile, uri)

                    dialogTitle = "Success"
                    val fileName = safePdfFile.name
                    dialogText = "\"$fileName\" is now stored at:\n${uri.toString()}"
                    showDialog = true
                }
            }
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .shaderBackground(InkFlow)

    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.Center)
                .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                .padding(16.dp)

        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Filename") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = editableText,
                onValueChange = { editableText = it },
                label = { Text("Recognized Text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = Int.MAX_VALUE
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = saveLocally, onCheckedChange = { saveLocally = it })
                Text("Save Locally")
            }



            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = saveToCloud, onCheckedChange = { saveToCloud = it })
                Text("Upload to Firebase (inactive)")
            }

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    if (saveLocally) {


                        CoroutineScope(Dispatchers.IO).launch {
                            val validatedTitle = sanitizeFileName(title)
                            val generatedPdf = createPdfFile(context, validatedTitle, editableText)
                            withContext(Dispatchers.Main) {
                                pdfFile = generatedPdf
                                createDocumentLauncher.launch("$validatedTitle.pdf")
                            }
                        }
                    }
                }
            ) {
                Text("Finish")
            }
        }

        if (showDialog) {
            AlertDialogDocument(
                onDismissRequest = {

                },
                onConfirmation = {
                    showDialog = false

                },
                dialogTitle = dialogTitle,
                dialogText = dialogText,
                icon = null
            )
        }
    }
}

fun sanitizeFileName(name: String): String {
    return name.replace(Regex("[^a-zA-Z0-9-_ ]"), "_").trim()
}


fun createPdfFile(context: Context, fileName: String, textContent: String): File {
    val pdfFile = File(context.filesDir, "$fileName.pdf")
    val pdfWriter = PdfWriter(pdfFile)
    val pdfDocument = PdfDocument(pdfWriter)
    val document = Document(pdfDocument)
    document.add(Paragraph(textContent))
    document.close()
    pdfDocument.close()
    return pdfFile
}


fun copyFileToUri(context: Context, sourceFile: File, destUri: Uri) {
    context.contentResolver.openOutputStream(destUri)?.use { outputStream ->
        sourceFile.inputStream().use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}


@Composable
fun AlertDialogDocument(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector?
) {
    AlertDialog(
        icon = {
            if (icon != null) {
                Icon(icon, contentDescription = "Example Icon")
            }
        },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text("Confirm")
            }
        }
    )
}
