package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "broadcasts",
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
data class BroadcastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val botId: Long,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val recipientsCount: Int = 0,
    val status: String = "Delivered"
)
