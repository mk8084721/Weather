package com.example.weather.Repo

import com.example.weather.model.CurrentWeather
import com.example.weather.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepo(var remoteDataSource:ApiService) {
    suspend fun getCurrentWeather(lat:Double , lon:Double):Flow<CurrentWeather>{
        return flow {
            val currentWeather = remoteDataSource.getCurrentWeather(lat , lon)
            emit(currentWeather)
        }
    }
}