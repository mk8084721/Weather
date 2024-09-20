package com.example.weather.model

data class Temp(
    var temp : Float,
    var feels_like : Float,
    var temp_min : Float,
    var temp_max : Float,
    var pressure : Int,
    var humidity : Int,
    var sea_level : Int,
    var grnd_level : Int
)
