package com.phinix.androidfinalprojectia.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface ChatMessageDao {

    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE userName = :userName ORDER BY id ASC")
    suspend fun getMessagesForUser(userName: String): List<ChatMessageEntity>

    @Query("DELETE FROM chat_messages WHERE userName = :userName")
    suspend fun deleteMessagesForUser(userName: String)

    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)
}