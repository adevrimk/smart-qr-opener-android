package com.smartqropener.core.security

import com.smartqropener.core.model.SecurityFlag
import com.smartqropener.core.model.UserSettings
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityCheckerTest {
    private val checker = SecurityChecker()

    @Test
    fun flagsShortLinkAndIpAndRedirectPatterns() {
        val flags = checker.check("http://192.168.0.10/redirect/login")
        assertTrue(flags.contains(SecurityFlag.NAKED_IP))
        assertTrue(flags.contains(SecurityFlag.REDIRECT_RISK))
    }

    @Test
    fun flagsCommonShorteners() {
        val flags = checker.check("https://bit.ly/smart-qr")
        assertTrue(flags.contains(SecurityFlag.SHORT_LINK))
    }

    @Test
    fun doesNotFlagNormalHttpsLinks() {
        val flags = checker.check("https://openai.com")
        assertFalse(flags.contains(SecurityFlag.SUSPICIOUS_DOMAIN))
        assertFalse(flags.contains(SecurityFlag.SHORT_LINK))
        assertFalse(flags.contains(SecurityFlag.NAKED_IP))
    }

    @Test
    fun trustedDomainsSkipWarnings() {
        val settings = UserSettings(trustedDomainsCsv = "wikipedia.org")
        val flags = checker.check("http://en.m.wikipedia.org", settings)
        assertTrue(flags.isEmpty())
    }
}
