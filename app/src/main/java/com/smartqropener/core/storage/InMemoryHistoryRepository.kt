package com.smartqropener.core.storage

import com.smartqropener.core.model.HistoryItem
import com.smartqropener.core.model.ScanResult
import com.smartqropener.core.model.ScanSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryHistoryRepository : HistoryRepository {
    private val items = MutableStateFlow<List<HistoryItem>>(emptyList())
    private var nextId: Long = 1

    override fun observeAll(): Flow<List<HistoryItem>> = items.asStateFlow()

    override suspend fun save(result: ScanResult, source: ScanSource) {
        val item = HistoryItem(
            id = nextId++,
            rawValue = result.rawValue,
            normalizedValue = result.normalizedValue,
            type = result.type,
            source = source,
            createdAt = result.timestamp,
            isSuspicious = result.isSuspicious,
            displayText = result.resolvedAction?.label,
        )
        items.update { listOf(item) + it }
    }

    override suspend fun toggleFavorite(id: Long, favorite: Boolean) {
        items.update { list ->
            list.map { if (it.id == id) it.copy(isFavorite = favorite) else it }
        }
    }

    override suspend fun delete(id: Long) {
        items.update { list -> list.filterNot { it.id == id } }
    }

    override suspend fun clear() {
        items.value = emptyList()
    }
}

