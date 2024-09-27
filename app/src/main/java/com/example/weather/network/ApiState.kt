package com.example.weather.network

import com.example.weather.database.model.Alert
import com.example.weather.database.model.HomeWeather
import com.example.weather.database.model.Hourly
import com.example.weather.network.model.CurrentWeather
import com.example.weather.network.model.ForecastWeather

open class ApiState {
    class Success(val data: CurrentWeather):ApiState()
    class ForecastSuccess(val data: ForecastWeather):ApiState()
    class LocalSuccess(val data: List<HomeWeather>):ApiState()
    class LocalHourlySuccess(val data: List<Hourly>):ApiState()
    class AlertSuccess(val data: List<Alert>):ApiState()
    class Failure(val msg:Throwable):ApiState()
    object Loading : ApiState()
}
