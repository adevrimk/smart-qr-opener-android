package com.smartqropener.history

import com.smartqropener.core.model.HistoryItem
import com.smartqropener.core.model.ScanType

data class HistoryUiState(
    val items: List<HistoryItem> = emptyList(),
    val query: String = "",
    val filter: HistoryFilter = HistoryFilter.ALL,
) {
    val filteredItems: List<HistoryItem> = items.filter { item ->
        item.matchesQuery(query) && item.matchesFilter(filter)
    }

    val totalCount: Int = items.size
    val favoriteCount: Int = items.count { it.isFavorite }
    val riskyCount: Int = items.count { it.isSuspicious }
    val actionableCount: Int = items.count { it.displayText != null }
}

private fun HistoryItem.matchesQuery(query: String): Boolean {
    val needle = query.trim().lowercase()
    if (needle.isBlank()) return true

    return rawValue.lowercase().contains(needle) ||
        normalizedValue.lowercase().contains(needle) ||
        type.name.lowercase().contains(needle) ||
        (displayText?.lowercase()?.contains(needle) == true)
}

private fun HistoryItem.matchesFilter(filter: HistoryFilter): Boolean {
    return when (filter) {
        HistoryFilter.ALL -> true
        HistoryFilter.FAVORITES -> isFavorite
        HistoryFilter.RISKY -> isSuspicious
        HistoryFilter.URL -> type == ScanType.URL
        HistoryFilter.AUTH -> type == ScanType.FIDO || type == ScanType.OTP
        HistoryFilter.COMMUNICATION -> type == ScanType.TEL || type == ScanType.EMAIL || type == ScanType.SMS
        HistoryFilter.ORGANIZER -> type == ScanType.CONTACT || type == ScanType.EVENT || type == ScanType.GEO
        HistoryFilter.WIFI -> type == ScanType.WIFI
        HistoryFilter.TEXT -> type == ScanType.TEXT || type == ScanType.UNKNOWN
    }
}
