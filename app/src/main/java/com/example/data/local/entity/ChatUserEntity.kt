package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_users",
    foreignKeys = [
        ForeignKey(
            entity = BotEntity::class,
            parentColumns = ["id"],
            childColumns = ["botId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["botId"]), Index(value = ["botId", "telegramUserId"], unique = true)]
)
data class ChatUserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val botId: Long,
    val telegramUserId: String,
    val firstName: String,
    val lastName: String = "",
    val username: String = "",
    val avatarColorHex: String = "#1A73E8",
    val lastMessageText: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isOnline: Boolean = true,
    val isResolved: Boolean = false
) {
    val displayName: String
        get() = when {
            firstName.isNotBlank() && lastName.isNotBlank() -> "$firstName $lastName"
            firstName.isNotBlank() -> firstName
            username.isNotBlank() -> "@$username"
            else -> "Telegram User #$telegramUserId"
        }
}
