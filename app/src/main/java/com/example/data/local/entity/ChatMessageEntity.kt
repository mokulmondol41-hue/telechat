package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["chatUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["chatUserId"]), Index(value = ["botId"])]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val botId: Long,
    val chatUserId: Long,
    val text: String,
    val isFromUser: Boolean, // true: received from customer, false: sent by support agent / bot
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "read", // "sent", "delivered", "read"
    val isAutoReply: Boolean = false
)
