package com.smartqropener.decoder

import com.smartqropener.core.model.ScanResult
import com.smartqropener.core.model.ScanSource
import com.smartqropener.core.model.ScanType
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class ScanParser {
    fun parse(rawValue: String, source: ScanSource = ScanSource.CAMERA): ScanResult {
        val normalized = normalize(rawValue)
        return ScanResult(
            rawValue = rawValue,
            normalizedValue = normalized,
            type = detectType(normalized),
            timestamp = System.currentTimeMillis(),
            source = source,
        )
    }

    fun detectType(value: String): ScanType {
        val trimmed = value.trim()
        val lower = trimmed.lowercase()

        return when {
            lower.startsWith("http://") || lower.startsWith("https://") -> ScanType.URL
            looksLikeBareUrl(lower) -> ScanType.URL
            lower.startsWith("fido:") || lower.startsWith("fido2:") || lower.startsWith("webauthn:") -> ScanType.FIDO
            lower.startsWith("otpauth:") || lower.startsWith("urn:ietf:params:oauth:") -> ScanType.OTP
            lower.startsWith("begin:vcard") || lower.startsWith("mecard:") -> ScanType.CONTACT
            lower.startsWith("begin:vcalendar") || lower.startsWith("begin:VEVENT".lowercase()) -> ScanType.EVENT
            lower.startsWith("tel:") -> ScanType.TEL
            lower.startsWith("mailto:") -> ScanType.EMAIL
            lower.startsWith("smsto:") || lower.startsWith("sms:") -> ScanType.SMS
            lower.startsWith("geo:") -> ScanType.GEO
            lower.startsWith("wifi:") || lower.contains("wpa:") -> ScanType.WIFI
            trimmed.isNotEmpty() -> ScanType.TEXT
            else -> ScanType.UNKNOWN
        }
    }

    private fun looksLikeBareUrl(value: String): Boolean {
        if (value.startsWith("www.")) return true
        if (value.contains(" ")) return false
        val hasDomainShape = Regex("""^[a-z0-9.-]+\.[a-z]{2,}([/:?].*)?$""").matches(value)
        return hasDomainShape
    }

    fun normalize(value: String): String {
        val trimmed = value.trim()
        return try {
            URLDecoder.decode(trimmed, StandardCharsets.UTF_8)
        } catch (_: IllegalArgumentException) {
            trimmed
        }
    }
}
