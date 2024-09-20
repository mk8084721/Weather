package com.example.weather.model

data class CurrentWeather(
    var coord: Crood,
    var weather: Array<WeatherStatus>,
    var base:String,
    var main: Temp,
    var visibility: Int,
    var wind : Wind,
    var id : Int,
    var timezone : Int,
    var name : String,
    var cod : Int
)
data class Wind(
    var speed : Float,
    var deg : Int,
    var gust : Float,
)
