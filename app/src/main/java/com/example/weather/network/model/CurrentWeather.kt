package com.example.weather.network.model

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
data class ForecastWeather( var list: Array<CurrentWeather> )
data class Coord(var longitude: Float , var latitude: Float)
data class Wind(
    var speed : Float,
    var deg : Int,
    var gust : Float,
)
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
data class WeatherStatus(
    var id : Int,
    var main : String,
    var description : String,
    var icon : String

)

