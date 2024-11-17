package com.phinix.androidfinalprojectia.common.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userName: String,
    val role: String,
    val content: String
)