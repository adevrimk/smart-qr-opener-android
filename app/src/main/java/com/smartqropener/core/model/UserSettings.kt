package com.smartqropener.core.model

data class UserSettings(
    val vibration: Boolean = true,
    val sound: Boolean = true,
    val safeMode: Boolean = true,
    val saveHistory: Boolean = true,
    val preferredBrowser: String? = null,
    val trustedDomainsCsv: String = "",
    val urlActionMode: ActionMode = ActionMode.OPEN,
    val wifiActionMode: ActionMode = ActionMode.OPEN,
    val authActionMode: ActionMode = ActionMode.OPEN,
    val contactActionMode: ActionMode = ActionMode.OPEN,
    val eventActionMode: ActionMode = ActionMode.OPEN,
    val otpActionMode: ActionMode = ActionMode.OPEN,
    val telActionMode: ActionMode = ActionMode.OPEN,
    val emailActionMode: ActionMode = ActionMode.OPEN,
    val smsActionMode: ActionMode = ActionMode.OPEN,
    val geoActionMode: ActionMode = ActionMode.OPEN,
    val textActionMode: ActionMode = ActionMode.COPY
)
