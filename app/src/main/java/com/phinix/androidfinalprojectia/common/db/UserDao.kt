package com.phinix.androidfinalprojectia.common.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

/**
 * DAO para realizar operaciones relacionadas con los usuarios.
 */
@Dao
interface UserDao {

    /**
     * Inserta un nuevo usuario en la base de datos.
     * @param user Entidad de usuario a insertar.
     */
    @Insert
    suspend fun insertUser(user: UserEntity)

    /**
     * Busca un usuario por su nombre y contraseña.
     * @param name Nombre del usuario.
     * @param password Contraseña del usuario.
     * @return Objeto `UserEntity` si las credenciales coinciden, o null si no.
     */
    @Query("SELECT * FROM users WHERE name = :name AND password = :password")
    suspend fun getUser(name: String, password: String): UserEntity?

    /**
     * Elimina un usuario específico de la base de datos.
     * @param user Entidad de usuario a eliminar.
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)

    /**
     * Elimina un usuario de la base de datos usando su nombre.
     * @param name Nombre del usuario a eliminar.
     */
    @Query("DELETE FROM users WHERE name = :name")
    suspend fun deleteUserByName(name: String)
}