package com.smartqropener.scanner

import com.smartqropener.core.model.HistoryItem
import com.smartqropener.core.model.ResolvedAction
import com.smartqropener.core.model.ScanResult
import com.smartqropener.core.model.SecurityFlag
import com.smartqropener.core.model.UserSettings

data class ScannerUiState(
    val isCameraPermissionGranted: Boolean = false,
    val isCameraReady: Boolean = false,
    val isTorchOn: Boolean = false,
    val isProcessing: Boolean = false,
    val currentScanResult: ScanResult? = null,
    val resolvedAction: ResolvedAction? = null,
    val securityFlags: List<SecurityFlag> = emptyList(),
    val recentHistory: List<HistoryItem> = emptyList(),
    val errorMessage: String? = null,
    val actionMessage: String? = null,
    val showOpenConfirmation: Boolean = false,
    val lastScanRawValue: String? = null,
    val lastScanAt: Long = 0L,
    val resultLockUntil: Long = 0L,
    val safeModeEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val saveHistoryEnabled: Boolean = true,
    val currentSettings: UserSettings = UserSettings(),
    val scanInput: String = "https://example.com"
)
