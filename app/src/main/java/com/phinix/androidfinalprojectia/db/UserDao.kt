package com.phinix.androidfinalprojectia.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE name = :name AND password = :password")
    suspend fun getUser(name: String, password: String): UserEntity?

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users WHERE name = :name")
    suspend fun deleteUserByName(name: String)
}