package com.smartqropener.actionengine

import com.smartqropener.core.model.ResolvedAction
import com.smartqropener.core.model.ActionMode
import com.smartqropener.core.model.ScanResult
import com.smartqropener.core.model.ScanType
import com.smartqropener.core.model.SecurityFlag
import com.smartqropener.core.model.UserSettings

class ActionEngine {
    fun resolve(
        result: ScanResult,
        flags: List<SecurityFlag> = emptyList(),
        settings: UserSettings = UserSettings(),
    ): ResolvedAction {
        val suspicious = flags.isNotEmpty()
        return when (result.type) {
            ScanType.URL -> ResolvedAction(
                kind = "url",
                label = labelForMode("link", settings.urlActionMode),
                payload = normalizeUrl(result.normalizedValue),
                requiresConfirmation = suspicious,
                preferredMode = settings.urlActionMode,
            )
            ScanType.FIDO -> ResolvedAction(
                kind = "fido",
                label = labelForMode("FIDO link", settings.authActionMode),
                payload = result.normalizedValue,
                preferredMode = settings.authActionMode,
            )
            ScanType.OTP -> ResolvedAction(
                kind = "otp",
                label = labelForMode("OTP", settings.otpActionMode),
                payload = result.normalizedValue,
                preferredMode = settings.otpActionMode,
            )
            ScanType.TEL -> ResolvedAction(
                kind = "tel",
                label = labelForMode("number", settings.telActionMode),
                payload = result.normalizedValue,
                preferredMode = settings.telActionMode,
            )
            ScanType.EMAIL -> ResolvedAction(
                kind = "email",
                label = labelForMode("email", settings.emailActionMode),
                payload = result.normalizedValue,
                preferredMode = settings.emailActionMode,
            )
            ScanType.SMS -> ResolvedAction(
                kind = "sms",
                label = labelForMode("message", settings.smsActionMode),
                payload = result.normalizedValue,
                preferredMode = settings.smsActionMode,
            )
            ScanType.GEO -> ResolvedAction(
                kind = "geo",
                label = labelForMode("map", settings.geoActionMode),
                payload = result.normalizedValue,
                preferredMode = settings.geoActionMode,
            )
            ScanType.WIFI -> ResolvedAction(
                kind = "wifi",
                label = labelForMode("Wi-Fi", settings.wifiActionMode),
                payload = result.normalizedValue,
                preferredMode = settings.wifiActionMode,
            )
            ScanType.CONTACT -> ResolvedAction(
                kind = "contact",
                label = labelForMode("contact", settings.contactActionMode),
                payload = result.normalizedValue,
                preferredMode = settings.contactActionMode,
            )
            ScanType.EVENT -> ResolvedAction(
                kind = "event",
                label = labelForMode("event", settings.eventActionMode),
                payload = result.normalizedValue,
                preferredMode = settings.eventActionMode,
            )
            ScanType.TEXT, ScanType.UNKNOWN -> ResolvedAction(
                kind = "text",
                label = labelForMode(
                    "text",
                    if (settings.textActionMode == ActionMode.OPEN) {
                        ActionMode.COPY
                    } else {
                        settings.textActionMode
                    },
                ),
                payload = result.normalizedValue,
                preferredMode = if (settings.textActionMode == ActionMode.OPEN) {
                    ActionMode.COPY
                } else {
                    settings.textActionMode
                },
            )
        }
    }

    private fun normalizeUrl(value: String): String {
        val trimmed = value.trim()
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }
    }

    private fun labelForMode(noun: String, mode: ActionMode): String {
        return when (mode) {
            ActionMode.OPEN -> "Open $noun"
            ActionMode.COPY -> "Copy $noun"
            ActionMode.SHARE -> "Share $noun"
        }
    }
}
