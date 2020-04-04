package com.example.parkinglot

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(Parqueo::class), version = 1, exportSchema = false)
public abstract class ParqueoRoomDatabase : RoomDatabase() {

    abstract fun parqueoDao(): ParqueoDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ParqueoRoomDatabase? = null

        fun getDatabase(context: Context): ParqueoRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParqueoRoomDatabase::class.java,
                    "parqueo_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}