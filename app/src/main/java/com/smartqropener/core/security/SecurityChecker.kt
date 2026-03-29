package com.smartqropener.core.security

import com.smartqropener.core.model.SecurityFlag
import java.net.URI
import com.smartqropener.core.model.UserSettings

class SecurityChecker {
    fun check(value: String, settings: UserSettings = UserSettings()): List<SecurityFlag> {
        val normalized = value.trim().lowercase()
        if (isTrusted(normalized, settings.trustedDomainsCsv)) {
            return emptyList()
        }
        val flags = mutableListOf<SecurityFlag>()

        if (hasShortLinkPattern(normalized)) flags += SecurityFlag.SHORT_LINK
        if (hasNakedIpPattern(normalized)) flags += SecurityFlag.NAKED_IP
        if (hasSuspiciousDomain(normalized)) flags += SecurityFlag.SUSPICIOUS_DOMAIN
        if (hasRedirectRisk(normalized)) flags += SecurityFlag.REDIRECT_RISK
        if (hasHomographRisk(normalized)) flags += SecurityFlag.HOMOGRAPH_RISK

        return flags
    }

    fun hasShortLinkPattern(value: String): Boolean {
        return value.contains("bit.ly") ||
            value.contains("t.co") ||
            value.contains("tinyurl") ||
            value.contains("goo.gl") ||
            value.contains("shorturl")
    }

    fun hasNakedIpPattern(value: String): Boolean {
        val ipRegex = Regex("""\b\d{1,3}(\.\d{1,3}){3}\b""")
        return ipRegex.containsMatchIn(value)
    }

    fun hasSuspiciousDomain(value: String): Boolean {
        val host = runCatching {
            val candidate = if (value.startsWith("http://") || value.startsWith("https://")) value else "https://$value"
            URI(candidate).host?.lowercase()
        }.getOrNull() ?: return false

        return host.contains("@") ||
            host.contains("xn--") ||
            host.count { it == '.' } > 3
    }

    fun hasRedirectRisk(value: String): Boolean {
        return value.contains("redirect") || value.contains("login") || value.contains("verify")
    }

    fun hasHomographRisk(value: String): Boolean {
        return value.contains("xn--")
    }

    fun isTrusted(value: String, trustedDomainsCsv: String): Boolean {
        val trusted = trustedDomainsCsv.split(',')
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
        if (trusted.isEmpty()) return false

        val host = runCatching {
            val candidate = if (value.startsWith("http://") || value.startsWith("https://")) value else "https://$value"
            URI(candidate).host?.lowercase()
        }.getOrNull() ?: return false

        return trusted.any { domain -> host == domain || host.endsWith(".$domain") }
    }
}
