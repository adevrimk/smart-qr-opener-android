package com.smartqropener.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.smartqropener.actionengine.ScanActionExecutor
import com.smartqropener.core.model.ActionMode
import com.smartqropener.core.model.ScanResult
import kotlinx.coroutines.delay

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel,
    actionExecutor: ScanActionExecutor,
    onOpenGallery: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val showScannerHint = rememberSaveable { mutableStateOf(true) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = viewModel::onPermissionChanged,
    )

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.onPermissionChanged(granted)
        if (!granted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(state.actionMessage) {
        state.actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        delay(4800)
        showScannerHint.value = false
    }

    LaunchedEffect(state.currentScanResult?.rawValue) {
        val current = state.currentScanResult ?: return@LaunchedEffect

        if (state.vibrationEnabled) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }

        if (state.soundEnabled) {
            runCatching {
                val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
                try {
                    tone.startTone(ToneGenerator.TONE_PROP_ACK, 120)
                } finally {
                    tone.release()
                }
            }
        }

        // auto-open is handled by a separate effect below
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            QuickActionsBar(
                onOpenGallery = onOpenGallery,
                onOpenHistory = onOpenHistory,
                onOpenSettings = onOpenSettings,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f),
                        ),
                    ),
                )
                .padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                HeroPanel(
                    cameraReady = state.isCameraReady,
                    safeModeEnabled = state.safeModeEnabled,
                    historyCount = state.recentHistory.size,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                ) {
                    CameraPreview(
                        isReady = state.isCameraReady,
                        hasPermission = state.isCameraPermissionGranted,
                        torchEnabled = state.isTorchOn,
                        onCameraReady = viewModel::onCameraReady,
                        onBarcodeDetected = viewModel::onBarcodeDetected,
                        modifier = Modifier.fillMaxSize(),
                    )
                    ScannerOverlay(
                        visible = showScannerHint.value,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 58.dp, start = 12.dp, end = 12.dp),
                    )
                    TorchActionBar(
                        isTorchOn = state.isTorchOn,
                        onTorchToggle = viewModel::onTorchToggle,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 84.dp),
                    )
                }

                state.currentScanResult?.let { result ->
                    LiveResultCard(
                        result = result,
                        safeModeEnabled = state.safeModeEnabled,
                        onOpenClick = {
                            if (result.requiresOpenConfirmation(state.safeModeEnabled)) {
                                viewModel.onOpenClick()
                            } else {
                                viewModel.onOpenOutcome(actionExecutor.perform(context, result.resolvedAction!!))
                            }
                        },
                        onCopyClick = {
                            result.resolvedAction?.let {
                                viewModel.onOpenOutcome(actionExecutor.perform(context, it.copy(preferredMode = ActionMode.COPY)))
                            }
                        },
                        onShareClick = {
                            result.resolvedAction?.let {
                                viewModel.onOpenOutcome(actionExecutor.perform(context, it.copy(preferredMode = ActionMode.SHARE)))
                            }
                        },
                    )
                }

                if (!state.isCameraPermissionGranted) {
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Grant camera permission")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (state.showOpenConfirmation) {
        val current = state.currentScanResult
        val action = current?.resolvedAction
        AlertDialog(
            onDismissRequest = viewModel::onSecurityCancel,
            title = { Text("Open this result?") },
            text = {
                Text(
                    if (state.safeModeEnabled && state.securityFlags.isNotEmpty()) {
                        "Safe mode is on, and this result looks suspicious. Review it before opening."
                    } else {
                        "This result needs confirmation before it opens."
                    },
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (action != null) {
                            viewModel.onOpenOutcome(actionExecutor.perform(context, action))
                        }
                        viewModel.onSecurityConfirm()
                    },
                    enabled = action != null,
                ) {
                    Text("Continue anyway")
                }
            },
            dismissButton = {
                Button(onClick = viewModel::onSecurityCancel) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun HeroPanel(
    cameraReady: Boolean,
    safeModeEnabled: Boolean,
    historyCount: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                        ),
                    ),
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Smart QR Opener", style = MaterialTheme.typography.headlineMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(text = if (cameraReady) "Camera ready" else "Camera waiting")
                StatusBadge(text = if (safeModeEnabled) "Safe mode on" else "Safe mode off")
                StatusBadge(text = "$historyCount saved")
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun QuickActionsBar(
    onOpenGallery: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        tonalElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(onClick = onOpenGallery) { Text("Gallery") }
                    FilledTonalButton(onClick = onOpenHistory) { Text("History") }
                    FilledTonalButton(onClick = onOpenSettings) { Text("Settings") }
                }
            }
        }
    }
}

@Composable
private fun TorchActionBar(
    isTorchOn: Boolean,
    onTorchToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onTorchToggle,
        modifier = modifier,
    ) {
        Text(if (isTorchOn) "Torch off" else "Torch on")
    }
}

@Composable
private fun LiveResultCard(
    result: ScanResult,
    safeModeEnabled: Boolean,
    onOpenClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    val action = result.resolvedAction
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(text = result.type.name.lowercase())
                StatusBadge(text = if (result.isSuspicious || safeModeEnabled) "review" else "ready")
            }
            Text(
                text = action?.label ?: "Scan result ready",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = result.normalizedValue,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onOpenClick,
                    enabled = action != null,
                ) {
                    Text("Open")
                }
                FilledTonalButton(
                    onClick = onCopyClick,
                    enabled = action != null,
                ) {
                    Text("Copy")
                }
                FilledTonalButton(
                    onClick = onShareClick,
                    enabled = action != null,
                ) {
                    Text("Share")
                }
            }
        }
    }
}

private fun ScanResult.requiresOpenConfirmation(safeModeEnabled: Boolean): Boolean {
    val action = resolvedAction ?: return false
    return action.requiresConfirmation || (safeModeEnabled && isSuspicious)
}
