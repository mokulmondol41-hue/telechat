package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.BotEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ChatUserEntity
import com.example.data.repository.BotRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatScreenUiState(
    val bot: BotEntity? = null,
    val users: List<ChatUserEntity> = emptyList(),
    val selectedUser: ChatUserEntity? = null,
    val messages: List<ChatMessageEntity> = emptyList(),
    val isTyping: Boolean = false,
    val userFilter: String = "ALL", // "ALL", "UNREAD", "RESOLVED"
    val userSearchQuery: String = "",
    val isLoading: Boolean = false
)

class ChatViewModel(
    private val repository: BotRepository,
    private val botId: Long,
    initialUserId: Long? = null
) : ViewModel() {

    private val _selectedUserId = MutableStateFlow<Long?>(initialUserId)
    private val _isTyping = MutableStateFlow(false)
    private val _userFilter = MutableStateFlow("ALL")
    private val _userSearchQuery = MutableStateFlow("")

    private var typingJob: Job? = null

    val botFlow = repository.getBotById(botId)
    val usersFlow = repository.getUsersForBot(botId)

    private val _currentMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    private var messageCollectionJob: Job? = null

    private val botAndUsersFlow = combine(
        botFlow,
        usersFlow,
        _selectedUserId
    ) { bot, users, selectedId ->
        BotAndUsers(bot, users, selectedId)
    }

    private val chatControlsFlow = combine(
        _currentMessages,
        _isTyping,
        _userFilter,
        _userSearchQuery
    ) { messages, isTyping, filter, query ->
        ChatControls(messages, isTyping, filter, query)
    }

    val uiState: StateFlow<ChatScreenUiState> = combine(
        botAndUsersFlow,
        chatControlsFlow
    ) { bu, controls ->
        val filteredUsers = bu.users.filter { user ->
            val matchesQuery = controls.query.isBlank() ||
                user.displayName.contains(controls.query, ignoreCase = true) ||
                user.username.contains(controls.query, ignoreCase = true) ||
                user.lastMessageText.contains(controls.query, ignoreCase = true)

            val matchesFilter = when (controls.filter) {
                "UNREAD" -> user.unreadCount > 0
                "RESOLVED" -> user.isResolved
                else -> true
            }
            matchesQuery && matchesFilter
        }

        val activeUser = bu.users.find { it.id == bu.selectedId } ?: bu.users.firstOrNull()

        ChatScreenUiState(
            bot = bu.bot,
            users = filteredUsers,
            selectedUser = activeUser,
            messages = controls.messages,
            isTyping = controls.isTyping,
            userFilter = controls.filter,
            userSearchQuery = controls.query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChatScreenUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            usersFlow.collect { users ->
                if (_selectedUserId.value == null && users.isNotEmpty()) {
                    selectUser(users.first().id)
                } else if (_selectedUserId.value != null && users.none { it.id == _selectedUserId.value }) {
                    if (users.isNotEmpty()) {
                        selectUser(users.first().id)
                    }
                }
            }
        }
    }

    fun selectUser(userId: Long) {
        _selectedUserId.value = userId
        messageCollectionJob?.cancel()
        messageCollectionJob = viewModelScope.launch {
            repository.markChatAsRead(userId)
            repository.getMessagesForUser(userId).collect { msgList ->
                _currentMessages.value = msgList
            }
        }
    }

    fun setUserFilter(filter: String) {
        _userFilter.value = filter
    }

    fun setUserSearchQuery(query: String) {
        _userSearchQuery.value = query
    }

    fun sendAgentReply(text: String) {
        val activeUserId = _selectedUserId.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            repository.sendAgentMessage(botId, activeUserId, text)
        }
    }

    fun simulateCustomerMessage(customerText: String) {
        val activeUserId = _selectedUserId.value ?: return
        if (customerText.isBlank()) return

        typingJob?.cancel()
        typingJob = viewModelScope.launch {
            _isTyping.value = true
            delay(1500)
            _isTyping.value = false
            repository.simulateIncomingCustomerMessage(
                botId = botId,
                chatUserId = activeUserId,
                customerText = customerText,
                coroutineScope = viewModelScope
            )
        }
    }

    fun createTestCustomer(name: String, handle: String, message: String) {
        viewModelScope.launch {
            val newUserId = repository.createNewChatUser(botId, name, handle, message)
            selectUser(newUserId)
        }
    }

    fun toggleResolvedStatus() {
        val user = uiState.value.selectedUser ?: return
        viewModelScope.launch {
            repository.toggleChatResolved(user.id, !user.isResolved)
        }
    }

    fun clearActiveChatHistory() {
        val user = uiState.value.selectedUser ?: return
        viewModelScope.launch {
            repository.clearChatHistory(user.id)
        }
    }

    companion object {
        fun provideFactory(
            repository: BotRepository,
            botId: Long,
            initialUserId: Long? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChatViewModel(repository, botId, initialUserId) as T
            }
        }
    }
}

private data class BotAndUsers(
    val bot: BotEntity?,
    val users: List<ChatUserEntity>,
    val selectedId: Long?
)

private data class ChatControls(
    val messages: List<ChatMessageEntity>,
    val isTyping: Boolean,
    val filter: String,
    val query: String
)
