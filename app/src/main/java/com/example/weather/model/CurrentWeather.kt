package com.example.weather.model

import java.util.Date

data class CurrentWeather(
    var coord: Coord,
    var weather: Array<WeatherStatus>,
    var base:String,
    var main: Temp,
    var visibility: Int,
    var wind : Wind,
    var id : Int,
    var timezone : Int,
    var name : String,
    var cod : Int,
    var dt_txt: String
)
data class Wind(
    var speed : Float,
    var deg : Int,
    var gust : Float,
)

data class ForecastWeather(
    var list: Array<CurrentWeather>
)
