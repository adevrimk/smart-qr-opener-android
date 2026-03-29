package com.smartqropener.core.storage

import com.smartqropener.core.model.HistoryItem
import com.smartqropener.core.model.ScanResult
import com.smartqropener.core.model.ScanSource
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun observeAll(): Flow<List<HistoryItem>>
    suspend fun save(result: ScanResult, source: ScanSource)
    suspend fun toggleFavorite(id: Long, favorite: Boolean)
    suspend fun delete(id: Long)
    suspend fun clear()
}

