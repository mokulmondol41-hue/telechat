package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "keyword_replies",
    foreignKeys = [
        ForeignKey(
            entity = BotEntity::class,
            parentColumns = ["id"],
            childColumns = ["botId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["botId"])]
)
data class KeywordReplyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val botId: Long,
    val keyword: String,
    val replyText: String,
    val matchType: String = "contains", // "contains" or "exact"
    val isEnabled: Boolean = true
)
