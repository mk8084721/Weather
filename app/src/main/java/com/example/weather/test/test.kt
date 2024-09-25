package com.example.weather.test

import com.example.weather.network.API
import com.example.weather.network.ApiService
import com.example.weather.network.model.ForecastWeather
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

suspend fun main(){
    var repo = allProductsRepo(API.retrofitService)
    coroutineScope {
        API.retrofitService.getWeatherForecast(45.133f,7.367f)
        repo.getAllRemoteProducts().collect{
            value -> println(value)
        }
    }
}

class allProductsRepo( var remoteDataSource: ApiService) {
    suspend fun getAllRemoteProducts(): Flow<ForecastWeather> {
        return flow {
            val weather = remoteDataSource.getWeatherForecast(45.133f, 7.367f) // This now returns a Products object
            emit(weather)
        }
    }
}