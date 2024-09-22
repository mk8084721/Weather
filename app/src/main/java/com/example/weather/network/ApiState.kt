package com.example.weather.network

import com.example.weather.model.CurrentWeather
import com.example.weather.model.ForecastWeather

open class ApiState {
    class Success(val data: CurrentWeather):ApiState()
    class ForecastSuccess(val data: ForecastWeather):ApiState()
    class Failure(val msg:Throwable):ApiState()
    object Loading : ApiState()
}
