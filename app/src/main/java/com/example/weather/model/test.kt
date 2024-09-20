package com.example.weather.model

import com.example.weather.network.API
import com.example.weather.network.ApiService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

suspend fun main(){
    var repo = allProductsRepo(API.retrofitService)
    coroutineScope {
        API.retrofitService.getCurrentWeather(45.133,7.367)
        repo.getAllRemoteProducts().collect{
            value -> println(value)
        }
    }
}

class allProductsRepo( var remoteDataSource: ApiService) {
    suspend fun getAllRemoteProducts(): Flow<CurrentWeather> {
        return flow {
            val weather = remoteDataSource.getCurrentWeather(45.133, 7.367) // This now returns a Products object
            emit(weather)
        }
    }
}