package com.example.parkinglot

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parqueo_table")

class Parqueo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "parqueadero") val parqueadero: String
)