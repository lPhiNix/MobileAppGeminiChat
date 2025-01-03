package com.phinix.androidfinalprojectia.common.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un mensaje de chat almacenado en la base de datos.
 * Define la estructura de la tabla `chat_messages`.
 */
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID autogenerado para identificar el mensaje.
    val userName: String, // Usuario asociado al mensaje.
    val role: String, // Rol del emisor del mensaje (e.g., "user" o "assistant").
    val content: String, // Contenido del mensaje.
    val createdAt: Long // Timestamp de la fecha y hora de creación
)