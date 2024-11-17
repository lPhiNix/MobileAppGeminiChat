package com.phinix.androidfinalprojectia.common.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatMessageDao {

    @Insert
    suspend fun insertMessage(chatMessage: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE userName = :userName ORDER BY id ASC")
    suspend fun getMessagesByUser(userName: String): List<ChatMessageEntity>

    @Query("DELETE FROM chat_messages WHERE userName = :userName")
    suspend fun deleteMessagesByUser(userName: String)

    @Query("DELETE FROM chat_messages WHERE userName = :userName")
    suspend fun deleteAllMessagesByUser(userName: String)
}