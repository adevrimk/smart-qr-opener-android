package com.smartqropener.core.model

data class ScanResult(
    val rawValue: String,
    val normalizedValue: String,
    val type: ScanType,
    val timestamp: Long,
    val source: ScanSource,
    val isSuspicious: Boolean = false,
    val resolvedAction: ResolvedAction? = null
)

