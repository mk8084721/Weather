package com.example.weather.database.model

import androidx.room.Entity

@Entity(primaryKeys = ["hour", "date"])
data class Hourly(
    val hour : String,
    val date : String,
    val temp : Float
)
