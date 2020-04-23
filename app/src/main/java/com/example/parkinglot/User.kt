package com.example.parkinglot

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var birthday: Timestamp? = null,
    var firstname: String? = "",
    var gender: Int? = 0,
    var lastname: String? = "",
    var phone_number: Int? = 0,
    var username: String? = "",
    var password: String? = "",
    var saldo: Int? = 0

)