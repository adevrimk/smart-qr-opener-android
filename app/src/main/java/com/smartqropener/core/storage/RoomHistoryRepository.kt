package com.smartqropener.core.storage

import android.content.Context
import androidx.room.Room
import com.smartqropener.core.model.HistoryItem
import com.smartqropener.core.model.ScanResult
import com.smartqropener.core.model.ScanSource
import com.smartqropener.core.model.ScanType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomHistoryRepository(context: Context) : HistoryRepository {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "smart_qr_history.db",
    ).build()

    private val dao = database.historyDao()

    override fun observeAll(): Flow<List<HistoryItem>> {
        return dao.observeAll().map { rows -> rows.map { it.toDomain() } }
    }

    override suspend fun save(result: ScanResult, source: ScanSource) {
        val entity = HistoryEntity(
            0L,
            result.rawValue,
            result.normalizedValue,
            result.type.name,
            source.name,
            result.timestamp,
            false,
            result.isSuspicious,
            result.resolvedAction?.label,
        )
        dao.insert(entity)
    }

    override suspend fun toggleFavorite(id: Long, favorite: Boolean) {
        dao.updateFavorite(id, favorite)
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun clear() {
        dao.clearAll()
    }

    private fun HistoryEntity.toDomain(): HistoryItem {
        return HistoryItem(
            id = id,
            rawValue = rawValue,
            normalizedValue = normalizedValue,
            type = ScanType.valueOf(type),
            source = ScanSource.valueOf(source),
            createdAt = createdAt,
            isFavorite = isFavorite,
            isSuspicious = isSuspicious,
            displayText = displayText,
        )
    }
}
