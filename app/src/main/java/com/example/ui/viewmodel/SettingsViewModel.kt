package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.BotEntity
import com.example.data.local.entity.BroadcastEntity
import com.example.data.local.entity.KeywordReplyEntity
import com.example.data.repository.BotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BotSettingsUiState(
    val bot: BotEntity? = null,
    val keywordReplies: List<KeywordReplyEntity> = emptyList(),
    val broadcasts: List<BroadcastEntity> = emptyList(),
    val isSaving: Boolean = false,
    val isBroadcastSending: Boolean = false
)

class SettingsViewModel(
    private val repository: BotRepository,
    private val botId: Long
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    private val _isBroadcastSending = MutableStateFlow(false)

    val botFlow = repository.getBotById(botId)
    val keywordRepliesFlow = repository.getKeywordReplies(botId)
    val broadcastsFlow = repository.getBroadcasts(botId)

    val uiState: StateFlow<BotSettingsUiState> = combine(
        botFlow,
        keywordRepliesFlow,
        broadcastsFlow,
        _isSaving,
        _isBroadcastSending
    ) { bot, keywords, broadcasts, saving, sending ->
        BotSettingsUiState(
            bot = bot,
            keywordReplies = keywords,
            broadcasts = broadcasts,
            isSaving = saving,
            isBroadcastSending = sending
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BotSettingsUiState()
    )

    fun updateWelcomeMessage(newMsg: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.updateWelcomeMessage(botId, newMsg)
            _isSaving.value = false
            onDone()
        }
    }

    fun updateOfflineReply(replyText: String, isEnabled: Boolean, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.updateOfflineReply(botId, replyText, isEnabled)
            _isSaving.value = false
            onDone()
        }
    }

    fun addKeywordReply(keyword: String, reply: String, matchType: String = "contains") {
        viewModelScope.launch {
            repository.addKeywordReply(botId, keyword, reply, matchType)
        }
    }

    fun toggleKeywordReply(ruleId: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleKeywordReply(ruleId, isEnabled)
        }
    }

    fun deleteKeywordReply(ruleId: Long) {
        viewModelScope.launch {
            repository.deleteKeywordReply(ruleId)
        }
    }

    fun sendBroadcast(message: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _isBroadcastSending.value = true
            repository.sendBroadcast(botId, message)
            _isBroadcastSending.value = false
            onComplete()
        }
    }

    fun deleteBot(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteBot(botId)
            onComplete()
        }
    }

    companion object {
        fun provideFactory(
            repository: BotRepository,
            botId: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(repository, botId) as T
            }
        }
    }
}
