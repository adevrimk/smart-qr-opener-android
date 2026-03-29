package com.smartqropener.core.model

data class HistoryItem(
    val id: Long,
    val rawValue: String,
    val normalizedValue: String,
    val type: ScanType,
    val source: ScanSource,
    val createdAt: Long,
    val isFavorite: Boolean = false,
    val isSuspicious: Boolean = false,
    val displayText: String? = null
)

