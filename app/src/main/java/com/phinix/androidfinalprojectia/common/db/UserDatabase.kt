package com.phinix.androidfinalprojectia.common.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class, ChatMessageEntity::class], version = 2)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"

                ) .fallbackToDestructiveMigration().build()

                INSTANCE = instance
                instance
            }
        }
    }
}