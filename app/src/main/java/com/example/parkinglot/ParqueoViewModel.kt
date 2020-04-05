package com.example.parkinglot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParqueoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ParqueoRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allParqueos: LiveData<List<Parqueo>>

    init {
        val parqueosDao = ParqueoRoomDatabase.getDatabase(application).parqueoDao()
        repository = ParqueoRepository(parqueosDao)
        allParqueos = repository.allWords
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(parqueos: Parqueo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(parqueos)
    }
}