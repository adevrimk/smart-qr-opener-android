package com.smartqropener.settings

import com.smartqropener.core.model.UserSettings
import com.smartqropener.core.model.ActionMode
import com.smartqropener.core.storage.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsStore: SettingsStore,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    init {
        settingsStore.observeSettings()
            .onEach { current ->
                _settings.value = current
            }
            .launchIn(scope)
    }

    fun setVibration(enabled: Boolean) {
        scope.launch { settingsStore.setVibration(enabled) }
    }

    fun setSound(enabled: Boolean) {
        scope.launch { settingsStore.setSound(enabled) }
    }

    fun setSafeMode(enabled: Boolean) {
        scope.launch { settingsStore.setSafeMode(enabled) }
    }

    fun setSaveHistory(enabled: Boolean) {
        scope.launch { settingsStore.setSaveHistory(enabled) }
    }

    fun setTrustedDomainsCsv(value: String) {
        scope.launch { settingsStore.setTrustedDomainsCsv(value) }
    }

    fun setUrlActionMode(mode: ActionMode) {
        scope.launch { settingsStore.setUrlActionMode(mode) }
    }

    fun setWifiActionMode(mode: ActionMode) {
        scope.launch { settingsStore.setWifiActionMode(mode) }
    }

    fun setAuthActionMode(mode: ActionMode) {
        scope.launch { settingsStore.setAuthActionMode(mode) }
    }

    fun setContactActionMode(mode: ActionMode) {
        scope.launch { settingsStore.setContactActionMode(mode) }
    }

    fun setEventActionMode(mode: ActionMode) {
        scope.launch { settingsStore.setEventActionMode(mode) }
    }

    fun setOtpActionMode(mode: ActionMode) {
        scope.launch { settingsStore.setOtpActionMode(mode) }
    }

    fun setTelActionMode(mode: ActionMode) {
        scope.launch { settingsStore.setTelActionMode(mode) }
    }

    fun setEmailActionMode(mode: ActionMode) {
        scope.launch { settingsStore.setEmailActionMode(mode) }
    }

    fun setSmsActionMode(mode: ActionMode) {
        scope.launch { settingsStore.setSmsActionMode(mode) }
    }

    fun setGeoActionMode(mode: ActionMode) {
        scope.launch { settingsStore.setGeoActionMode(mode) }
    }

    fun setTextActionMode(mode: ActionMode) {
        scope.launch { settingsStore.setTextActionMode(mode) }
    }
}
