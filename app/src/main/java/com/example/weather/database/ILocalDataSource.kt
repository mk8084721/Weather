package com.example.weather.database

import android.content.Context
import com.example.weather.database.model.Alert
import com.example.weather.database.model.Favorite
import com.example.weather.database.model.HomeWeather
import com.example.weather.database.model.Hourly
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    var context: Context
    fun getHomeWeather(): Flow<List<HomeWeather>>

    suspend fun insertHomeWeather(homeWeather: List<HomeWeather>)
    fun updateHomeWeather(homeWeather: List<HomeWeather>)
    fun deleteHomeWeather(homeWeather: List<HomeWeather>)
    fun getAllFavWeather(): Flow<List<Favorite>>
    fun insertFavWeather(favWeather: List<Favorite>)
    fun updateFavWeather(favWeather: List<Favorite>)
    fun deleteFavWeather(favWeather: List<Favorite>)
    fun getAlerts(): Flow<List<Alert>>

    suspend fun insertAlert(alert: Alert)

    //Hourly
    fun getHourlyWeather(): Flow<List<Hourly>>
    fun insertHourlyWeather(hourlyWeather: List<Hourly>)
    fun updateHourlyWeather(hourlyWeather: List<Hourly>)
    fun deleteHourlyWeather(hourlyWeather: List<Hourly>)
    fun clearHourlyTable()
}