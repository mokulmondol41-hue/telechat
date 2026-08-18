package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE chatUserId = :chatUserId ORDER BY timestamp ASC")
    fun getMessagesForUser(chatUserId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE chatUserId = :chatUserId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessagesForUser(chatUserId: Long, limit: Int = 50): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>): List<Long>

    @Query("DELETE FROM chat_messages WHERE chatUserId = :chatUserId")
    suspend fun deleteMessagesForUser(chatUserId: Long)

    @Query("DELETE FROM chat_messages WHERE botId = :botId")
    suspend fun deleteMessagesForBot(botId: Long)

    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)
}
