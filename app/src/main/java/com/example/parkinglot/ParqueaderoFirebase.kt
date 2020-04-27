package com.example.parkinglot
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class ParqueaderoFirebase(
    var city: String? = "",
    var closing_hour: String? = "",
    var country: String? = "",
    var departament: String? = "",
    var description: String? = "",
    var name: String? = "",
    var opening_hour: String? = "",
    var address: String? = "",
    var parking_bike_cost: Int? = 0,
    var parking_car_cost: Int? = 0,
    var parking_motorcycle_cost: Int? = 0,
    var lat: Double?=0.0,
    var long: Double?=0.0

) : Serializable