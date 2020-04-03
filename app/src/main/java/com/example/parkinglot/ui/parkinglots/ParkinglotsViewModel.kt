package com.example.parkinglot.ui.parkinglots

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ParkinglotsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        // value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}