package com.smartqropener.actionengine

import com.smartqropener.core.model.ResolvedAction
import com.smartqropener.core.model.ScanResult
import com.smartqropener.core.model.ScanSource
import com.smartqropener.core.model.ScanType
import com.smartqropener.core.model.SecurityFlag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActionEngineTest {
    private val engine = ActionEngine()

    @Test
    fun resolvesUrlWithConfirmationWhenSuspicious() {
        val result = ScanResult(
            rawValue = "example.com",
            normalizedValue = "example.com",
            type = ScanType.URL,
            timestamp = 0L,
            source = ScanSource.CAMERA,
        )

        val action = engine.resolve(result, listOf(SecurityFlag.SHORT_LINK))
        assertEquals("url", action.kind)
        assertEquals("https://example.com", action.payload)
        assertTrue(action.requiresConfirmation)
    }

    @Test
    fun resolvesTextToCopyAction() {
        val result = ScanResult(
            rawValue = "plain text",
            normalizedValue = "plain text",
            type = ScanType.TEXT,
            timestamp = 0L,
            source = ScanSource.GALLERY,
        )

        val action = engine.resolve(result)
        assertEquals("text", action.kind)
        assertEquals("Copy text", action.label)
    }

    @Test
    fun resolvesFidoAsOpenableLink() {
        val result = ScanResult(
            rawValue = "fido:/challenge",
            normalizedValue = "fido:/challenge",
            type = ScanType.FIDO,
            timestamp = 0L,
            source = ScanSource.CAMERA,
        )

        val action = engine.resolve(result)
        assertEquals("fido", action.kind)
        assertEquals("Open FIDO link", action.label)
    }
}
