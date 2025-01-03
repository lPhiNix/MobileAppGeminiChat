package com.phinix.androidfinalprojectia.common.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Clase abstracta que define la base de datos de Room, configurando las tablas y DAOs.
 */
@Database(entities = [UserEntity::class, ChatMessageEntity::class], version = 3)
abstract class UserDatabase : RoomDatabase() {

    // Referencias a los DAOs definidos.
    abstract fun userDao(): UserDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile private var INSTANCE: UserDatabase? = null

        /**
         * Devuelve una instancia única de la base de datos.
         * Usa un patrón de Singleton para evitar múltiples instancias.
         * @param context Contexto de la aplicación.
         * @return Instancia de `UserDatabase`.
         */
        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                )
                    .fallbackToDestructiveMigration() // Borra datos al cambiar la versión (solo en desarrollo).
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}