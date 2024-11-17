package com.phinix.androidfinalprojectia.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val name: String,
    val password: String,
    val apiKey: String
)
