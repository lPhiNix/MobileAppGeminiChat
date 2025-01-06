package com.phinix.androidfinalprojectia.common.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Data Access Object (DAO) para realizar operaciones CRUD con los mensajes de chat.
 */
@Dao
interface ChatMessageDao {

    /**
     * Inserta un nuevo mensaje en la base de datos.
     * @param chatMessage Objeto de tipo `ChatMessageEntity` a insertar.
     */
    @Insert
    suspend fun insertMessage(chatMessage: ChatMessageEntity)

    /**
     * Obtiene todos los mensajes asociados a un usuario específico.
     * @param userName Nombre del usuario cuyos mensajes se quieren obtener.
     * @return Lista de mensajes del usuario ordenados por su ID (ascendente).
     */
    @Query("SELECT * FROM chat_messages WHERE userName = :userName ORDER BY id ASC")
    suspend fun getMessagesByUser(userName: String): List<ChatMessageEntity>

    /**
     * Elimina todos los mensajes de un usuario específico.
     * @param userName Nombre del usuario cuyos mensajes se eliminarán.
     */
    @Query("DELETE FROM chat_messages WHERE userName = :userName")
    suspend fun deleteMessagesByUser(userName: String)

    /**
     * Alias adicional para eliminar todos los mensajes de un usuario.
     * @param userName Nombre del usuario.
     */
    @Query("DELETE FROM chat_messages WHERE userName = :userName")
    suspend fun deleteAllMessagesByUser(userName: String)

    @Query("SELECT * FROM chat_messages WHERE createdAt LIKE :date")
    fun getMessagesByDate(date: String): List<ChatMessageEntity>

    @Query("SELECT * FROM chat_messages")
    fun getAllMessages(): List<ChatMessageEntity>
}