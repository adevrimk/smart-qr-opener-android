package com.smartqropener.scanner

import com.smartqropener.actionengine.ActionEngine
import com.smartqropener.actionengine.OpenOutcome
import com.smartqropener.core.model.ScanSource
import com.smartqropener.core.model.ScanType
import com.smartqropener.core.security.SecurityChecker
import com.smartqropener.core.storage.HistoryRepository
import com.smartqropener.core.storage.SettingsStore
import com.smartqropener.decoder.ScanParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val parser: ScanParser,
    private val securityChecker: SecurityChecker,
    private val actionEngine: ActionEngine,
    private val historyRepository: HistoryRepository,
    private val settingsStore: SettingsStore,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    init {
        historyRepository.observeAll()
            .onEach { history ->
                _uiState.value = _uiState.value.copy(recentHistory = history)
            }
            .launchIn(scope)

        settingsStore.observeSettings()
            .onEach { settings ->
                _uiState.value = _uiState.value.copy(
                    safeModeEnabled = settings.safeMode,
                    vibrationEnabled = settings.vibration,
                    soundEnabled = settings.sound,
                    saveHistoryEnabled = settings.saveHistory,
                    currentSettings = settings,
                )
            }
            .launchIn(scope)
    }

    fun onPermissionChanged(granted: Boolean) {
        _uiState.value = _uiState.value.copy(isCameraPermissionGranted = granted)
    }

    fun onCameraReady() {
        _uiState.value = _uiState.value.copy(isCameraReady = true)
    }

    fun onTorchToggle() {
        _uiState.value = _uiState.value.copy(isTorchOn = !_uiState.value.isTorchOn)
    }

    fun onBarcodeDetected(rawValue: String, source: ScanSource = ScanSource.CAMERA) {
        val now = System.currentTimeMillis()
        val currentState = _uiState.value
        if (now < currentState.resultLockUntil) {
            return
        }
        if (currentState.lastScanRawValue == rawValue && now - currentState.lastScanAt < 1500L) {
            return
        }

        val settings = settingsStore.currentSettings()
        val parsed = parser.parse(rawValue, source)
        val trustedUrl = parsed.type == ScanType.URL &&
            securityChecker.isTrusted(parsed.normalizedValue, settings.trustedDomainsCsv)

        val flags = if (parsed.type == ScanType.URL && !trustedUrl) {
            securityChecker.check(parsed.normalizedValue, settings)
        } else {
            emptyList()
        }
        val action = actionEngine.resolve(parsed, flags, settings)

        val updated = parsed.copy(
            isSuspicious = flags.isNotEmpty(),
            resolvedAction = action,
        )

        _uiState.value = _uiState.value.copy(
            isProcessing = false,
            currentScanResult = updated,
            resolvedAction = action,
            securityFlags = flags,
            errorMessage = null,
            actionMessage = "${action.label} detected.",
            lastScanRawValue = rawValue,
            lastScanAt = now,
            resultLockUntil = now + 1800L,
            scanInput = rawValue,
        )

        if (_uiState.value.saveHistoryEnabled) {
            scope.launch {
                historyRepository.save(updated, source)
            }
        }
    }

    fun onScanInputChanged(value: String) {
        _uiState.value = _uiState.value.copy(scanInput = value)
    }

    fun onDemoScanClick() {
        onBarcodeDetected(_uiState.value.scanInput)
    }

    fun onCopyClick() {
        _uiState.value = _uiState.value.copy(actionMessage = "Copied to clipboard.")
    }

    fun onShareClick() {
        _uiState.value = _uiState.value.copy(actionMessage = "Share sheet opened.")
    }

    fun onOpenClick() {
        val action = _uiState.value.resolvedAction
        val needsConfirmation = action != null && requiresConfirmationForCurrentResult()
        _uiState.value = _uiState.value.copy(
            showOpenConfirmation = needsConfirmation,
            actionMessage = if (needsConfirmation) {
                "This result needs confirmation before opening."
            } else {
                null
            },
        )
    }

    fun onOpenOutcome(outcome: OpenOutcome) {
        _uiState.value = _uiState.value.copy(
            actionMessage = when (outcome) {
                OpenOutcome.OPENED -> "Opened successfully."
                OpenOutcome.FALLBACK_USED -> "Primary app missing, fallback opened."
                OpenOutcome.COPIED -> "No app handler found, copied to clipboard."
                OpenOutcome.SHARED -> "Share sheet opened."
                OpenOutcome.FAILED -> "Could not open this result."
            },
        )
    }

    fun onSecurityConfirm() {
        _uiState.value = _uiState.value.copy(
            showOpenConfirmation = false,
            actionMessage = "Security confirmation accepted.",
        )
    }

    fun onSecurityCancel() {
        _uiState.value = _uiState.value.copy(
            showOpenConfirmation = false,
            actionMessage = "Security confirmation cancelled.",
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null, actionMessage = null)
    }

    fun close() {
        scope.cancel()
    }

    private fun requiresConfirmationForCurrentResult(): Boolean {
        val current = _uiState.value
        val action = current.resolvedAction ?: return false
        return action.requiresConfirmation || (current.safeModeEnabled && current.securityFlags.isNotEmpty())
    }

}
