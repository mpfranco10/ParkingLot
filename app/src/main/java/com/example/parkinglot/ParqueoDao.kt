package com.example.parkinglot

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ParqueoDao {

    @Query("SELECT * from parqueo_table ORDER BY parqueadero ASC")
    fun getOrderedParqueos(): LiveData<List<Parqueo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(parqueo: Parqueo)

    @Query("DELETE FROM parqueo_table")
    suspend fun deleteAll()
}