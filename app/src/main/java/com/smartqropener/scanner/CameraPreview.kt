package com.smartqropener.scanner

import android.annotation.SuppressLint
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.smartqropener.decoder.BarcodeAnalyzer
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    isReady: Boolean,
    hasPermission: Boolean,
    torchEnabled: Boolean,
    onCameraReady: () -> Unit,
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!hasPermission) {
        Box(
            modifier = modifier
                .fillMaxWidth()
        )
        return
    }

    CameraPreviewContent(
        isReady = isReady,
        torchEnabled = torchEnabled,
        onCameraReady = onCameraReady,
        onBarcodeDetected = onBarcodeDetected,
        modifier = modifier,
    )
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun CameraPreviewContent(
    isReady: Boolean,
    torchEnabled: Boolean,
    onCameraReady: () -> Unit,
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val analyzer = remember(onBarcodeDetected) { BarcodeAnalyzer(onBarcodeDetected) }
    val cameraProviderFuture = remember(context) { ProcessCameraProvider.getInstance(context) }

    DisposableEffect(cameraExecutor) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    DisposableEffect(lifecycleOwner, previewView, analyzer, torchEnabled) {
        val executor = ContextCompat.getMainExecutor(context)
        val listener = Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val camera = bindUseCases(
                cameraProvider = cameraProvider,
                lifecycleOwner = lifecycleOwner,
                previewView = previewView,
                analyzer = analyzer,
                executor = cameraExecutor,
            )
            camera.cameraControl.enableTorch(torchEnabled)
            if (!isReady) {
                onCameraReady()
            }
        }

        cameraProviderFuture.addListener(listener, executor)

        onDispose {
            if (cameraProviderFuture.isDone) {
                cameraProviderFuture.get().unbindAll()
            }
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxWidth(),
        update = { },
    )
}

private fun bindUseCases(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView,
    analyzer: BarcodeAnalyzer,
    executor: java.util.concurrent.ExecutorService,
): Camera {
    val preview = Preview.Builder().build().also {
        it.surfaceProvider = previewView.surfaceProvider
    }

    val analysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also {
            it.setAnalyzer(executor, analyzer)
        }

    cameraProvider.unbindAll()
    return cameraProvider.bindToLifecycle(
        lifecycleOwner,
        CameraSelector.DEFAULT_BACK_CAMERA,
        preview,
        analysis,
    )
}
