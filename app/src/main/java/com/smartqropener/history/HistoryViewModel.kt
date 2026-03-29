package com.smartqropener.history

import com.smartqropener.core.model.HistoryItem
import com.smartqropener.core.storage.HistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val historyRepository: HistoryRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val items = MutableStateFlow<List<HistoryItem>>(emptyList())
    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(HistoryFilter.ALL)

    val uiState: StateFlow<HistoryUiState> = combine(items, query, filter) { items, query, filter ->
        HistoryUiState(
            items = items,
            query = query,
            filter = filter,
        )
    }.stateIn(scope, SharingStarted.Eagerly, HistoryUiState())

    init {
        historyRepository.observeAll()
            .onEach { history ->
                items.value = history
            }
            .launchIn(scope)
    }

    fun onQueryChanged(value: String) {
        query.value = value
    }

    fun onFilterChanged(value: HistoryFilter) {
        filter.value = value
    }

    fun toggleFavorite(item: HistoryItem) {
        scope.launch {
            historyRepository.toggleFavorite(item.id, !item.isFavorite)
        }
    }

    fun delete(item: HistoryItem) {
        scope.launch {
            historyRepository.delete(item.id)
        }
    }

    fun clear() {
        scope.launch {
            historyRepository.clear()
        }
    }

    fun close() {
        scope.cancel()
    }
}
