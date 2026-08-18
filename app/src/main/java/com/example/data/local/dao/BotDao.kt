package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BotDao {
    @Query("SELECT * FROM bots ORDER BY createdAt DESC")
    fun getAllBots(): Flow<List<BotEntity>>

    @Query("SELECT * FROM bots WHERE id = :id LIMIT 1")
    fun getBotById(id: Long): Flow<BotEntity?>

    @Query("SELECT * FROM bots WHERE id = :id LIMIT 1")
    suspend fun getBotByIdDirect(id: Long): BotEntity?

    @Query("SELECT COUNT(*) FROM bots")
    fun getBotCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBot(bot: BotEntity): Long

    @Update
    suspend fun updateBot(bot: BotEntity)

    @Delete
    suspend fun deleteBot(bot: BotEntity)

    @Query("DELETE FROM bots WHERE id = :id")
    suspend fun deleteBotById(id: Long)

    @Query("UPDATE bots SET isOnline = :isOnline WHERE id = :id")
    suspend fun updateBotOnlineStatus(id: Long, isOnline: Boolean)

    @Query("UPDATE bots SET welcomeMessage = :welcomeMessage WHERE id = :id")
    suspend fun updateWelcomeMessage(id: Long, welcomeMessage: String)

    @Query("UPDATE bots SET offlineReplyMessage = :offlineReply, isOfflineReplyEnabled = :isEnabled WHERE id = :id")
    suspend fun updateOfflineReply(id: Long, offlineReply: String, isEnabled: Boolean)
}
