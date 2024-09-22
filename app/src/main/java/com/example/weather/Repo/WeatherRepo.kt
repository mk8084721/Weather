package com.example.weather.Repo

import com.example.weather.model.CurrentWeather
import com.example.weather.model.ForecastWeather
import com.example.weather.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepo(var remoteDataSource:ApiService) {
    suspend fun getCurrentWeather(lon:Double , lat:Double):Flow<CurrentWeather>{
        return flow {
            val currentWeather = remoteDataSource.getCurrentWeather(lon , lat)
            emit(currentWeather)
        }
    }

    fun getWeatherForecast(lon: Double, lat: Double): Flow<ForecastWeather> {
        return flow {
            val forecastWeather = remoteDataSource.getWeatherForecast(lon , lat)
            emit(forecastWeather)
        }
    }
}