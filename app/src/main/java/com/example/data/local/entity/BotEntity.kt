package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bots")
data class BotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val botName: String,
    val username: String,
    val token: String,
    val welcomeMessage: String = "Hello! 👋 Welcome to our support channel. How can we assist you today?",
    val offlineReplyMessage: String = "Our team is currently away. We have received your message and will respond shortly.",
    val isOnline: Boolean = true,
    val isOfflineReplyEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
