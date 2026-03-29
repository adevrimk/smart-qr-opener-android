package com.smartqropener.gallery

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smartqropener.core.model.ScanSource
import com.smartqropener.decoder.GalleryBarcodeScanner
import com.smartqropener.scanner.ScannerViewModel
import kotlinx.coroutines.launch

@Composable
fun GalleryScreen(
    scannerViewModel: ScannerViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scanner = remember { GalleryBarcodeScanner() }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var status by remember { mutableStateOf("Pick an image with a QR code.") }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        selectedUri = uri
        if (uri == null) {
            status = "No image selected."
            return@rememberLauncherForActivityResult
        }

        status = "Scanning image..."
        scope.launch {
            val value = scanner.scan(context, uri)
            if (value.isNullOrBlank()) {
                status = "No QR code found in the image."
            } else {
                scannerViewModel.onBarcodeDetected(value, ScanSource.GALLERY)
                status = "QR code found and sent to scanner."
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Gallery scan", style = MaterialTheme.typography.headlineMedium)
        Text(status)
        Button(
            onClick = {
                picker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
        ) {
            Text("Pick image")
        }
        Button(onClick = onBack) {
            Text("Back")
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Selected image")
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Text(selectedUri?.toString() ?: "None")
            }
        }
    }
}

