package com.example.weather.Repo

import android.content.Context
import com.example.weather.database.LocalDataSource
import com.example.weather.database.model.Alert
import com.example.weather.database.model.Favorite
import com.example.weather.database.model.HomeWeather
import com.example.weather.database.model.Hourly
import com.example.weather.network.ApiService
import com.example.weather.network.model.CurrentWeather
import com.example.weather.network.model.ForecastWeather
import kotlinx.coroutines.flow.Flow

interface IWeatherRepo {
    suspend fun getCurrentWeather(lon: Float, lat: Float): Flow<CurrentWeather>
    fun getWeatherForecast(lon: Float, lat: Float): Flow<ForecastWeather>
    fun getHomeWeather(): Flow<List<HomeWeather>>

    suspend fun insertHomeWeather(homeWeather: List<HomeWeather>)
    fun updateHomeWeather(homeWeather: List<HomeWeather>)
    fun getAllFavWeather(): Flow<List<Favorite>>
    fun insertFavWeather(list: List<Favorite>)
    fun deleteFavWeather(list: List<Favorite>)
    fun saveLocationToPreferences(context: Context, latitude: Float, longitude: Float)
    fun getLocationFromPreferences(context: Context): Pair<Float, Float>
    fun getAlerts(): Flow<List<Alert>>
    suspend fun insertAlert(alert: Alert)
    fun clearHourlyTable()
    fun insertHourlyWeather(hourlyWeather: MutableList<Hourly>)
    fun getHourlyWeather(): Flow<List<Hourly>>
    fun insertDefaultSettings(context: Context)
    fun getDefaultSettings(context: Context): List<String?>
    fun getLangFromPreferences(context: Context): String?
    fun deleteAlert(alert: Alert)
}