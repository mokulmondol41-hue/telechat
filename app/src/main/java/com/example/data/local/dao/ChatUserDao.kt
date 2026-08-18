package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ChatUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatUserDao {
    @Query("SELECT * FROM chat_users WHERE botId = :botId ORDER BY lastMessageTimestamp DESC")
    fun getUsersForBot(botId: Long): Flow<List<ChatUserEntity>>

    @Query("SELECT * FROM chat_users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: Long): Flow<ChatUserEntity?>

    @Query("SELECT * FROM chat_users WHERE id = :userId LIMIT 1")
    suspend fun getUserByIdDirect(userId: Long): ChatUserEntity?

    @Query("SELECT * FROM chat_users WHERE botId = :botId AND telegramUserId = :tgId LIMIT 1")
    suspend fun getUserByTelegramId(botId: Long, tgId: String): ChatUserEntity?

    @Query("SELECT SUM(unreadCount) FROM chat_users WHERE botId = :botId")
    fun getTotalUnreadCountForBot(botId: Long): Flow<Int?>

    @Query("SELECT SUM(unreadCount) FROM chat_users")
    fun getTotalUnreadCountAllBots(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM chat_users WHERE botId = :botId")
    fun getUserCountForBot(botId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM chat_users")
    fun getTotalActiveChats(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: ChatUserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<ChatUserEntity>): List<Long>

    @Update
    suspend fun updateUser(user: ChatUserEntity)

    @Query("UPDATE chat_users SET lastMessageText = :text, lastMessageTimestamp = :time WHERE id = :userId")
    suspend fun updateLastMessage(userId: Long, text: String, time: Long)

    @Query("UPDATE chat_users SET unreadCount = 0 WHERE id = :userId")
    suspend fun clearUnreadCount(userId: Long)

    @Query("UPDATE chat_users SET isResolved = :isResolved WHERE id = :userId")
    suspend fun updateResolvedStatus(userId: Long, isResolved: Boolean)

    @Delete
    suspend fun deleteUser(user: ChatUserEntity)
}
