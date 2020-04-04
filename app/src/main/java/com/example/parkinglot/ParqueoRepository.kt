package com.example.parkinglot

import androidx.lifecycle.LiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ParqueoRepository(private val parqueoDao: ParqueoDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allWords: LiveData<List<Parqueo>> = parqueoDao.getOrderedParqueos()

    suspend fun insert(parqueo: Parqueo) {
        parqueoDao.insert(parqueo)
    }
}