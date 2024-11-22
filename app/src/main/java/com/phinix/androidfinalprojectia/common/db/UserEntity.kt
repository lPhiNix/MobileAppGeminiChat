package com.phinix.androidfinalprojectia.common.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un usuario almacenado en la base de datos.
 * Define la estructura de la tabla `users`.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val name: String, // Nombre del usuario (clave primaria).
    val password: String, // Contraseña del usuario.
    val apiKey: String // Clave API asociada al usuario.
)