package com.example.weather.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HomeWeather (
    @PrimaryKey
    var id : Int,
    var lon : Float,
    var lat : Float,
    var locationName : String,
    var date : String,
    var weatherConditionEn : String,
    var weatherConditionAr : String,
    var weatherTemp : Float,
    var hourTemp : Float,
    var hour : String,
    var pressure : Int,
    var humidity : Int,
    var windSpeed : Float,
    var clouds : Int
)