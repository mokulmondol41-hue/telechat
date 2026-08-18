package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.BroadcastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BroadcastDao {
    @Query("SELECT * FROM broadcasts WHERE botId = :botId ORDER BY timestamp DESC")
    fun getBroadcastsForBot(botId: Long): Flow<List<BroadcastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBroadcast(broadcast: BroadcastEntity): Long

    @Delete
    suspend fun deleteBroadcast(broadcast: BroadcastEntity)
}
