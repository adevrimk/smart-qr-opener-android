package com.smartqropener.decoder

import com.smartqropener.core.model.ScanType
import org.junit.Assert.assertEquals
import org.junit.Test

class ScanParserTest {
    private val parser = ScanParser()

    @Test
    fun detectsCommonQrTypes() {
        assertEquals(ScanType.URL, parser.detectType("https://example.com"))
        assertEquals(ScanType.URL, parser.detectType("www.example.com"))
        assertEquals(ScanType.URL, parser.detectType("example.com"))
        assertEquals(ScanType.FIDO, parser.detectType("fido:/challenge"))
        assertEquals(ScanType.FIDO, parser.detectType("webauthn:/challenge"))
        assertEquals(ScanType.TEL, parser.detectType("tel:+15551234567"))
        assertEquals(ScanType.EMAIL, parser.detectType("mailto:test@example.com"))
        assertEquals(ScanType.SMS, parser.detectType("smsto:+15551234567"))
        assertEquals(ScanType.GEO, parser.detectType("geo:37.7749,-122.4194"))
        assertEquals(ScanType.WIFI, parser.detectType("WIFI:T:WPA;S:Guest;P:secret;;"))
    }

    @Test
    fun decodesEncodedTextAndFallsBackToText() {
        val result = parser.parse("Hello%20World")
        assertEquals("Hello World", result.normalizedValue)
        assertEquals(ScanType.TEXT, result.type)
    }
}
