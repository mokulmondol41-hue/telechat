package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.BotDao
import com.example.data.local.dao.BroadcastDao
import com.example.data.local.dao.ChatMessageDao
import com.example.data.local.dao.ChatUserDao
import com.example.data.local.dao.KeywordReplyDao
import com.example.data.local.entity.BotEntity
import com.example.data.local.entity.BroadcastEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ChatUserEntity
import com.example.data.local.entity.KeywordReplyEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BotEntity::class,
        ChatUserEntity::class,
        ChatMessageEntity::class,
        KeywordReplyEntity::class,
        BroadcastEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun botDao(): BotDao
    abstract fun chatUserDao(): ChatUserDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun keywordReplyDao(): KeywordReplyDao
    abstract fun broadcastDao(): BroadcastDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "telechat_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(db: AppDatabase) {
            val botDao = db.botDao()
            val userDao = db.chatUserDao()
            val messageDao = db.chatMessageDao()
            val keywordDao = db.keywordReplyDao()
            val broadcastDao = db.broadcastDao()

            // Preload Bot 1: SupportHub Bot
            val bot1Id = botDao.insertBot(
                BotEntity(
                    botName = "SupportHub Bot",
                    username = "supporthub_official_bot",
                    token = "6829402914:AAH928k_dKwlQ9284jksZla829x",
                    welcomeMessage = "Welcome to SupportHub! We are here 24/7 to resolve your inquiries. Type your question or type /help.",
                    offlineReplyMessage = "Our agents are currently offline for maintenance. Your inquiry has been logged with ticket priority.",
                    isOnline = true,
                    isOfflineReplyEnabled = true
                )
            )

            // Preload Bot 2: CryptoAlert & Trade Bot
            val bot2Id = botDao.insertBot(
                BotEntity(
                    botName = "CryptoPulse Assistant",
                    username = "cryptopulse_trade_bot",
                    token = "7193049182:AAEnkd90284nmkdLZ82940182",
                    welcomeMessage = "Welcome to CryptoPulse Alerts! Get real-time price alerts, portfolio summaries, and trade signals.",
                    offlineReplyMessage = "Automated market triggers are active. Live human support is offline.",
                    isOnline = true,
                    isOfflineReplyEnabled = false
                )
            )

            // Preload Bot 3: ShopAssist Store Bot
            val bot3Id = botDao.insertBot(
                BotEntity(
                    botName = "ShopAssist Store",
                    username = "shopassist_orders_bot",
                    token = "5920194829:AAJlq8293nfls_9384kflw291",
                    welcomeMessage = "Hey! Thanks for visiting our store. Need help tracking an order, discounts, or returns?",
                    offlineReplyMessage = "Our store support is closed for the evening. We reopen at 8:00 AM.",
                    isOnline = false,
                    isOfflineReplyEnabled = true
                )
            )

            // Preload Users for Bot 1
            val now = System.currentTimeMillis()
            val user1Id = userDao.insertUser(
                ChatUserEntity(
                    botId = bot1Id,
                    telegramUserId = "9820194",
                    firstName = "Sarah",
                    lastName = "Connor",
                    username = "sconnor",
                    avatarColorHex = "#1A73E8",
                    lastMessageText = "Can I upgrade my monthly subscription to the Enterprise plan?",
                    lastMessageTimestamp = now - 1000 * 60 * 3, // 3 mins ago
                    unreadCount = 2,
                    isOnline = true,
                    isResolved = false
                )
            )

            val user2Id = userDao.insertUser(
                ChatUserEntity(
                    botId = bot1Id,
                    telegramUserId = "4729103",
                    firstName = "Michael",
                    lastName = "Chang",
                    username = "mchang_tech",
                    avatarColorHex = "#22C55E",
                    lastMessageText = "Thanks for resolving the webhook configuration!",
                    lastMessageTimestamp = now - 1000 * 60 * 45, // 45 mins ago
                    unreadCount = 0,
                    isOnline = false,
                    isResolved = true
                )
            )

            val user3Id = userDao.insertUser(
                ChatUserEntity(
                    botId = bot1Id,
                    telegramUserId = "8301928",
                    firstName = "Elena",
                    lastName = "Rostova",
                    username = "elena_r",
                    avatarColorHex = "#F59E0B",
                    lastMessageText = "Is there an API rate limit for batch message exports?",
                    lastMessageTimestamp = now - 1000 * 60 * 180, // 3 hours ago
                    unreadCount = 1,
                    isOnline = true,
                    isResolved = false
                )
            )

            // Preload Messages for Sarah
            messageDao.insertMessages(
                listOf(
                    ChatMessageEntity(
                        botId = bot1Id,
                        chatUserId = user1Id,
                        text = "Hello, I have a quick question regarding billing.",
                        isFromUser = true,
                        timestamp = now - 1000 * 60 * 15,
                        status = "read"
                    ),
                    ChatMessageEntity(
                        botId = bot1Id,
                        chatUserId = user1Id,
                        text = "Hi Sarah! I'd be happy to help you with that. What would you like to know?",
                        isFromUser = false,
                        timestamp = now - 1000 * 60 * 12,
                        status = "read"
                    ),
                    ChatMessageEntity(
                        botId = bot1Id,
                        chatUserId = user1Id,
                        text = "Can I upgrade my monthly subscription to the Enterprise plan?",
                        isFromUser = true,
                        timestamp = now - 1000 * 60 * 3,
                        status = "delivered"
                    ),
                    ChatMessageEntity(
                        botId = bot1Id,
                        chatUserId = user1Id,
                        text = "Also, will my remaining credits roll over automatically?",
                        isFromUser = true,
                        timestamp = now - 1000 * 60 * 2,
                        status = "delivered"
                    )
                )
            )

            // Preload Messages for Michael
            messageDao.insertMessages(
                listOf(
                    ChatMessageEntity(
                        botId = bot1Id,
                        chatUserId = user2Id,
                        text = "Our webhook endpoint returned a 502 error earlier today.",
                        isFromUser = true,
                        timestamp = now - 1000 * 60 * 120,
                        status = "read"
                    ),
                    ChatMessageEntity(
                        botId = bot1Id,
                        chatUserId = user2Id,
                        text = "We have cleared the retry queue and verified your SSL certificate handshake.",
                        isFromUser = false,
                        timestamp = now - 1000 * 60 * 60,
                        status = "read"
                    ),
                    ChatMessageEntity(
                        botId = bot1Id,
                        chatUserId = user2Id,
                        text = "Thanks for resolving the webhook configuration!",
                        isFromUser = true,
                        timestamp = now - 1000 * 60 * 45,
                        status = "read"
                    )
                )
            )

            // Preload Messages for Elena
            messageDao.insertMessages(
                listOf(
                    ChatMessageEntity(
                        botId = bot1Id,
                        chatUserId = user3Id,
                        text = "Is there an API rate limit for batch message exports?",
                        isFromUser = true,
                        timestamp = now - 1000 * 60 * 180,
                        status = "delivered"
                    )
                )
            )

            // Preload Keyword Rules for Bot 1
            keywordDao.insertKeywordReplies(
                listOf(
                    KeywordReplyEntity(
                        botId = bot1Id,
                        keyword = "pricing",
                        replyText = "Our plans: Starter ($19/mo), Pro ($49/mo), and Enterprise (Custom). Learn more at telechat.io/pricing",
                        matchType = "contains",
                        isEnabled = true
                    ),
                    KeywordReplyEntity(
                        botId = bot1Id,
                        keyword = "help",
                        replyText = "Available commands:\n- /pricing: View subscription packages\n- /status: Check system uptime\n- /human: Connect with live agent",
                        matchType = "exact",
                        isEnabled = true
                    ),
                    KeywordReplyEntity(
                        botId = bot1Id,
                        keyword = "refund",
                        replyText = "All refund requests are processed within 3-5 business days. Please provide your invoice #ID.",
                        matchType = "contains",
                        isEnabled = true
                    )
                )
            )

            // Preload Broadcast for Bot 1
            broadcastDao.insertBroadcast(
                BroadcastEntity(
                    botId = bot1Id,
                    messageText = "Scheduled maintenance notice: Live chat will undergo routine updates this Sunday from 02:00 to 03:00 UTC.",
                    timestamp = now - 1000 * 60 * 60 * 24, // 1 day ago
                    recipientsCount = 142,
                    status = "Delivered"
                )
            )

            // Preload Users for Bot 2
            val user4Id = userDao.insertUser(
                ChatUserEntity(
                    botId = bot2Id,
                    telegramUserId = "5910283",
                    firstName = "David",
                    lastName = "Miller",
                    username = "dmiller_eth",
                    avatarColorHex = "#0D47A1",
                    lastMessageText = "Alert triggered: BTC crossed $96,000 threshold",
                    lastMessageTimestamp = now - 1000 * 60 * 10,
                    unreadCount = 1,
                    isOnline = true,
                    isResolved = false
                )
            )
            messageDao.insertMessage(
                ChatMessageEntity(
                    botId = bot2Id,
                    chatUserId = user4Id,
                    text = "Alert triggered: BTC crossed $96,000 threshold",
                    isFromUser = true,
                    timestamp = now - 1000 * 60 * 10,
                    status = "delivered"
                )
            )
        }
    }
}
