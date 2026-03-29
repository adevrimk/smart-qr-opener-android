package com.smartqropener.core.storage

import com.smartqropener.core.model.ActionMode
import com.smartqropener.core.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsStore {
    private val state = MutableStateFlow(UserSettings())

    fun observeSettings(): Flow<UserSettings> = state.asStateFlow()

    fun currentSettings(): UserSettings = state.value

    suspend fun setVibration(enabled: Boolean) {
        state.update { it.copy(vibration = enabled) }
    }

    suspend fun setSound(enabled: Boolean) {
        state.update { it.copy(sound = enabled) }
    }

    suspend fun setSafeMode(enabled: Boolean) {
        state.update { it.copy(safeMode = enabled) }
    }

    suspend fun setSaveHistory(enabled: Boolean) {
        state.update { it.copy(saveHistory = enabled) }
    }

    suspend fun setPreferredBrowser(packageName: String?) {
        state.update { it.copy(preferredBrowser = packageName) }
    }

    suspend fun setTrustedDomainsCsv(value: String) {
        state.update { it.copy(trustedDomainsCsv = value) }
    }

    suspend fun setUrlActionMode(mode: ActionMode) {
        state.update { it.copy(urlActionMode = mode) }
    }

    suspend fun setWifiActionMode(mode: ActionMode) {
        state.update { it.copy(wifiActionMode = mode) }
    }

    suspend fun setAuthActionMode(mode: ActionMode) {
        state.update { it.copy(authActionMode = mode) }
    }

    suspend fun setContactActionMode(mode: ActionMode) {
        state.update { it.copy(contactActionMode = mode) }
    }

    suspend fun setEventActionMode(mode: ActionMode) {
        state.update { it.copy(eventActionMode = mode) }
    }

    suspend fun setOtpActionMode(mode: ActionMode) {
        state.update { it.copy(otpActionMode = mode) }
    }

    suspend fun setTelActionMode(mode: ActionMode) {
        state.update { it.copy(telActionMode = mode) }
    }

    suspend fun setEmailActionMode(mode: ActionMode) {
        state.update { it.copy(emailActionMode = mode) }
    }

    suspend fun setSmsActionMode(mode: ActionMode) {
        state.update { it.copy(smsActionMode = mode) }
    }

    suspend fun setGeoActionMode(mode: ActionMode) {
        state.update { it.copy(geoActionMode = mode) }
    }

    suspend fun setTextActionMode(mode: ActionMode) {
        state.update { it.copy(textActionMode = mode) }
    }
}
