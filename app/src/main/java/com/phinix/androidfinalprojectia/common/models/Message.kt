package com.phinix.androidfinalprojectia.common.models

/**
 * Representa un mensaje de chat en la lógica de la aplicación.
 * Se usa para manipular datos en el dominio (fuera de la base de datos).
 *
 * @property role Indica el rol del emisor del mensaje (e.g., "user", "assistant").
 * @property content Texto del mensaje.
 */
data class Message(
    val role: String,
    val content: String,
    val createAt: Long
)