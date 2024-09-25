package com.example.weather.database.model

import androidx.room.Entity

@Entity(primaryKeys = ["lon", "lat"])
data class Favorite (
    var lon : Double,
    var lat : Double,
    var locationName : String
)