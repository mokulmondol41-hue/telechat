package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.BotEntity
import com.example.data.local.entity.BroadcastEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ChatUserEntity
import com.example.data.local.entity.KeywordReplyEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BotRepository(private val database: AppDatabase) {

    private val botDao = database.botDao()
    private val chatUserDao = database.chatUserDao()
    private val chatMessageDao = database.chatMessageDao()
    private val keywordReplyDao = database.keywordReplyDao()
    private val broadcastDao = database.broadcastDao()

    // Bots
    val allBots: Flow<List<BotEntity>> = botDao.getAllBots()
    val botCount: Flow<Int> = botDao.getBotCount()
    val totalActiveChats: Flow<Int> = chatUserDao.getTotalActiveChats()
    val totalUnreadCount: Flow<Int?> = chatUserDao.getTotalUnreadCountAllBots()

    fun getBotById(botId: Long): Flow<BotEntity?> = botDao.getBotById(botId)

    suspend fun insertBot(
        name: String,
        username: String,
        token: String,
        welcomeMsg: String
    ): Long = withContext(Dispatchers.IO) {
        val cleanUsername = username.removePrefix("@").trim()
        val bot = BotEntity(
            botName = name.trim(),
            username = cleanUsername,
            token = token.trim(),
            welcomeMessage = if (welcomeMsg.isBlank()) {
                "Hello! Welcome to our support channel. How can we assist you today?"
            } else {
                welcomeMsg.trim()
            },
            offlineReplyMessage = "Our agents are currently offline. Your inquiry has been saved.",
            isOnline = true,
            isOfflineReplyEnabled = true
        )
        val botId = botDao.insertBot(bot)

        // Seed default keyword reply for the new bot
        keywordReplyDao.insertKeywordReply(
            KeywordReplyEntity(
                botId = botId,
                keyword = "help",
                replyText = "Hi! Type your question and our support team will respond shortly.",
                matchType = "contains",
                isEnabled = true
            )
        )

        // Create a welcome demo user for testing
        val userId = chatUserDao.insertUser(
            ChatUserEntity(
                botId = botId,
                telegramUserId = "1049281",
                firstName = "Demo",
                lastName = "User",
                username = "demo_tester",
                avatarColorHex = "#1A73E8",
                lastMessageText = "/start",
                lastMessageTimestamp = System.currentTimeMillis(),
                unreadCount = 0,
                isOnline = true
            )
        )

        chatMessageDao.insertMessage(
            ChatMessageEntity(
                botId = botId,
                chatUserId = userId,
                text = "/start",
                isFromUser = true,
                timestamp = System.currentTimeMillis() - 5000,
                status = "read"
            )
        )

        chatMessageDao.insertMessage(
            ChatMessageEntity(
                botId = botId,
                chatUserId = userId,
                text = bot.welcomeMessage,
                isFromUser = false,
                timestamp = System.currentTimeMillis(),
                status = "read",
                isAutoReply = true
            )
        )

        botId
    }

    suspend fun updateBotStatus(botId: Long, isOnline: Boolean) = withContext(Dispatchers.IO) {
        botDao.updateBotOnlineStatus(botId, isOnline)
    }

    suspend fun updateWelcomeMessage(botId: Long, welcomeMessage: String) = withContext(Dispatchers.IO) {
        botDao.updateWelcomeMessage(botId, welcomeMessage.trim())
    }

    suspend fun updateOfflineReply(botId: Long, message: String, isEnabled: Boolean) = withContext(Dispatchers.IO) {
        botDao.updateOfflineReply(botId, message.trim(), isEnabled)
    }

    suspend fun deleteBot(botId: Long) = withContext(Dispatchers.IO) {
        botDao.deleteBotById(botId)
    }

    // Chat Users
    fun getUsersForBot(botId: Long): Flow<List<ChatUserEntity>> = chatUserDao.getUsersForBot(botId)
    fun getUserById(userId: Long): Flow<ChatUserEntity?> = chatUserDao.getUserById(userId)
    fun getUnreadCountForBot(botId: Long): Flow<Int?> = chatUserDao.getTotalUnreadCountForBot(botId)

    suspend fun markChatAsRead(userId: Long) = withContext(Dispatchers.IO) {
        chatUserDao.clearUnreadCount(userId)
    }

    suspend fun toggleChatResolved(userId: Long, isResolved: Boolean) = withContext(Dispatchers.IO) {
        chatUserDao.updateResolvedStatus(userId, isResolved)
    }

    // Chat Messages
    fun getMessagesForUser(chatUserId: Long): Flow<List<ChatMessageEntity>> =
        chatMessageDao.getMessagesForUser(chatUserId)

    suspend fun sendAgentMessage(
        botId: Long,
        chatUserId: Long,
        text: String
    ): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val msg = ChatMessageEntity(
            botId = botId,
            chatUserId = chatUserId,
            text = text.trim(),
            isFromUser = false,
            timestamp = now,
            status = "sent"
        )
        val msgId = chatMessageDao.insertMessage(msg)
        chatUserDao.updateLastMessage(chatUserId, text.trim(), now)
        msgId
    }

    suspend fun clearChatHistory(chatUserId: Long) = withContext(Dispatchers.IO) {
        chatMessageDao.deleteMessagesForUser(chatUserId)
        chatUserDao.updateLastMessage(chatUserId, "Chat cleared", System.currentTimeMillis())
        chatUserDao.clearUnreadCount(chatUserId)
    }

    // Inbound customer simulation + automated response triggers
    suspend fun simulateIncomingCustomerMessage(
        botId: Long,
        chatUserId: Long,
        customerText: String,
        coroutineScope: CoroutineScope
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val msg = ChatMessageEntity(
            botId = botId,
            chatUserId = chatUserId,
            text = customerText.trim(),
            isFromUser = true,
            timestamp = now,
            status = "delivered"
        )
        chatMessageDao.insertMessage(msg)
        chatUserDao.updateLastMessage(chatUserId, customerText.trim(), now)

        // Check Auto-Reply and Keyword triggers
        val bot = botDao.getBotByIdDirect(botId)
        val user = chatUserDao.getUserByIdDirect(chatUserId)

        if (bot != null && user != null) {
            coroutineScope.launch(Dispatchers.IO) {
                delay(1200) // Realistic typing delay

                if (!bot.isOnline && bot.isOfflineReplyEnabled && bot.offlineReplyMessage.isNotBlank()) {
                    // Send Offline auto reply
                    val replyTimestamp = System.currentTimeMillis()
                    chatMessageDao.insertMessage(
                        ChatMessageEntity(
                            botId = botId,
                            chatUserId = chatUserId,
                            text = "🤖 [Auto-Reply] " + bot.offlineReplyMessage,
                            isFromUser = false,
                            timestamp = replyTimestamp,
                            status = "read",
                            isAutoReply = true
                        )
                    )
                    chatUserDao.updateLastMessage(chatUserId, "🤖 [Auto-Reply] " + bot.offlineReplyMessage, replyTimestamp)
                } else {
                    // Check keyword rules
                    val activeKeywords = keywordReplyDao.getActiveKeywordRepliesDirect(botId)
                    val textLower = customerText.lowercase().trim()
                    var matchedReply: String? = null

                    for (rule in activeKeywords) {
                        val key = rule.keyword.lowercase().trim()
                        if (rule.matchType == "exact") {
                            if (textLower == key || textLower == "/$key") {
                                matchedReply = rule.replyText
                                break
                            }
                        } else {
                            if (textLower.contains(key)) {
                                matchedReply = rule.replyText
                                break
                            }
                        }
                    }

                    if (matchedReply != null) {
                        val replyTimestamp = System.currentTimeMillis()
                        chatMessageDao.insertMessage(
                            ChatMessageEntity(
                                botId = botId,
                                chatUserId = chatUserId,
                                text = "🤖 [Keyword Rule] $matchedReply",
                                isFromUser = false,
                                timestamp = replyTimestamp,
                                status = "read",
                                isAutoReply = true
                            )
                        )
                        chatUserDao.updateLastMessage(chatUserId, "🤖 [Keyword Rule] $matchedReply", replyTimestamp)
                    }
                }
            }
        }
    }

    // Keyword Replies
    fun getKeywordReplies(botId: Long): Flow<List<KeywordReplyEntity>> =
        keywordReplyDao.getKeywordRepliesForBot(botId)

    suspend fun addKeywordReply(
        botId: Long,
        keyword: String,
        reply: String,
        matchType: String = "contains"
    ) = withContext(Dispatchers.IO) {
        keywordReplyDao.insertKeywordReply(
            KeywordReplyEntity(
                botId = botId,
                keyword = keyword.trim(),
                replyText = reply.trim(),
                matchType = matchType,
                isEnabled = true
            )
        )
    }

    suspend fun toggleKeywordReply(id: Long, isEnabled: Boolean) = withContext(Dispatchers.IO) {
        keywordReplyDao.toggleKeywordReply(id, isEnabled)
    }

    suspend fun deleteKeywordReply(id: Long) = withContext(Dispatchers.IO) {
        keywordReplyDao.deleteKeywordReplyById(id)
    }

    // Broadcasts
    fun getBroadcasts(botId: Long): Flow<List<BroadcastEntity>> =
        broadcastDao.getBroadcastsForBot(botId)

    suspend fun sendBroadcast(botId: Long, message: String): Long = withContext(Dispatchers.IO) {
        val users = chatUserDao.getUsersForBot(botId).firstOrNull() ?: emptyList()
        val now = System.currentTimeMillis()

        // Insert into Broadcast history
        val broadcastId = broadcastDao.insertBroadcast(
            BroadcastEntity(
                botId = botId,
                messageText = message.trim(),
                timestamp = now,
                recipientsCount = users.size,
                status = "Delivered"
            )
        )

        // Broadcast to all active chat users
        users.forEach { user ->
            chatMessageDao.insertMessage(
                ChatMessageEntity(
                    botId = botId,
                    chatUserId = user.id,
                    text = "📢 [Broadcast] " + message.trim(),
                    isFromUser = false,
                    timestamp = now,
                    status = "read",
                    isAutoReply = false
                )
            )
            chatUserDao.updateLastMessage(user.id, "📢 [Broadcast] " + message.trim(), now)
        }

        broadcastId
    }

    // Add a new chat user for testing
    suspend fun createNewChatUser(
        botId: Long,
        name: String,
        username: String,
        initialMessage: String
    ): Long = withContext(Dispatchers.IO) {
        val colors = listOf("#1A73E8", "#22C55E", "#F59E0B", "#8B5CF6", "#EC4899", "#06B6D4")
        val randomColor = colors.random()
        val now = System.currentTimeMillis()

        val userId = chatUserDao.insertUser(
            ChatUserEntity(
                botId = botId,
                telegramUserId = (1000000..9999999).random().toString(),
                firstName = name.trim(),
                username = username.removePrefix("@").trim(),
                avatarColorHex = randomColor,
                lastMessageText = initialMessage.trim(),
                lastMessageTimestamp = now,
                unreadCount = 1,
                isOnline = true
            )
        )

        chatMessageDao.insertMessage(
            ChatMessageEntity(
                botId = botId,
                chatUserId = userId,
                text = initialMessage.trim(),
                isFromUser = true,
                timestamp = now,
                status = "delivered"
            )
        )

        userId
    }
}
