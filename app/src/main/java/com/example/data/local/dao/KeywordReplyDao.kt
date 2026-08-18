package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.KeywordReplyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordReplyDao {
    @Query("SELECT * FROM keyword_replies WHERE botId = :botId ORDER BY id ASC")
    fun getKeywordRepliesForBot(botId: Long): Flow<List<KeywordReplyEntity>>

    @Query("SELECT * FROM keyword_replies WHERE botId = :botId AND isEnabled = 1")
    suspend fun getActiveKeywordRepliesDirect(botId: Long): List<KeywordReplyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeywordReply(reply: KeywordReplyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeywordReplies(replies: List<KeywordReplyEntity>): List<Long>

    @Update
    suspend fun updateKeywordReply(reply: KeywordReplyEntity)

    @Query("UPDATE keyword_replies SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun toggleKeywordReply(id: Long, isEnabled: Boolean)

    @Query("DELETE FROM keyword_replies WHERE id = :id")
    suspend fun deleteKeywordReplyById(id: Long)

    @Delete
    suspend fun deleteKeywordReply(reply: KeywordReplyEntity)
}
