package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.BotEntity
import com.example.data.repository.BotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val bots: List<BotEntity> = emptyList(),
    val totalBots: Int = 0,
    val activeChats: Int = 0,
    val unreadCount: Int = 0,
    val searchQuery: String = "",
    val filterStatus: String = "ALL", // "ALL", "ONLINE", "OFFLINE"
    val isLoading: Boolean = false
)

class BotViewModel(private val repository: BotRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterStatus = MutableStateFlow("ALL")
    val filterStatus = _filterStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val botStatsFlow = combine(
        repository.allBots,
        repository.botCount,
        repository.totalActiveChats,
        repository.totalUnreadCount
    ) { bots, count, activeChats, unread ->
        BotStats(bots, count, activeChats, unread ?: 0)
    }

    private val filterFlow = combine(
        _searchQuery,
        _filterStatus,
        _isLoading
    ) { query, filter, loading ->
        FilterState(query, filter, loading)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        botStatsFlow,
        filterFlow
    ) { stats, filters ->
        val filteredBots = stats.bots.filter { bot ->
            val matchesQuery = filters.query.isBlank() ||
                bot.botName.contains(filters.query, ignoreCase = true) ||
                bot.username.contains(filters.query, ignoreCase = true)
            val matchesFilter = when (filters.filter) {
                "ONLINE" -> bot.isOnline
                "OFFLINE" -> !bot.isOnline
                else -> true
            }
            matchesQuery && matchesFilter
        }
        DashboardUiState(
            bots = filteredBots,
            totalBots = stats.count,
            activeChats = stats.activeChats,
            unreadCount = stats.unread,
            searchQuery = filters.query,
            filterStatus = filters.filter,
            isLoading = filters.loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterStatusChanged(status: String) {
        _filterStatus.value = status
    }

    fun toggleBotOnline(botId: Long, currentOnline: Boolean) {
        viewModelScope.launch {
            repository.updateBotStatus(botId, !currentOnline)
        }
    }

    fun addBot(
        name: String,
        username: String,
        token: String,
        welcomeMsg: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val botId = repository.insertBot(name, username, token, welcomeMsg)
            _isLoading.value = false
            onSuccess(botId)
        }
    }

    fun deleteBot(botId: Long) {
        viewModelScope.launch {
            repository.deleteBot(botId)
        }
    }

    companion object {
        fun provideFactory(repository: BotRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BotViewModel(repository) as T
                }
            }
    }
}

private data class BotStats(
    val bots: List<BotEntity>,
    val count: Int,
    val activeChats: Int,
    val unread: Int
)

private data class FilterState(
    val query: String,
    val filter: String,
    val loading: Boolean
)
