package com.example.weather.database

import android.content.Context
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.weather.database.model.Alert
import com.example.weather.database.model.Favorite
import com.example.weather.database.model.HomeWeather
import com.example.weather.database.model.Hourly
import kotlinx.coroutines.flow.Flow

class LocalDataSource(var context: Context) {
    var dao = WeatherDB.getInstance(context).getWeatherDao()

    fun getHomeWeather(): Flow<List<HomeWeather>> {
        return dao.getHomeWeather()
    }

    suspend fun insertHomeWeather(homeWeather: List<HomeWeather>) {
        dao.insertHomeWeather(homeWeather)
    }

    fun updateHomeWeather(homeWeather: List<HomeWeather>) {
        dao.updateHomeWeather(homeWeather)
    }

    fun deleteHomeWeather(homeWeather: List<HomeWeather>) {
        dao.deleteHomeWeather(homeWeather)
    }

    fun getAllFavWeather(): Flow<List<Favorite>> {
        return dao.getAllFavWeather()
    }

    fun insertFavWeather(favWeather: List<Favorite>) {
        dao.insertFavWeather(favWeather)
    }

    fun updateFavWeather(favWeather: List<Favorite>) {
        dao.updateFavWeather(favWeather)
    }

    fun deleteFavWeather(favWeather: List<Favorite>) {
        dao.deleteFavWeather(favWeather)
    }

    fun getAlerts(): Flow<List<Alert>> {
        return dao.getAllAlerts()
    }

    suspend fun insertAlert(alert: Alert) {
        dao.insertAlert(alert)
    }

    //Hourly
    fun getHourlyWeather() : Flow<List<Hourly>>{
        return dao.getHourlyWeather()
    }
    fun insertHourlyWeather(hourlyWeather : List<Hourly>){
        dao.insertHourlyWeather(hourlyWeather)
    }
    fun updateHourlyWeather(hourlyWeather : List<Hourly>){
        dao.updateHourlyWeather(hourlyWeather)
    }
    fun deleteHourlyWeather(hourlyWeather : List<Hourly>){
        dao.deleteHourlyWeather(hourlyWeather)
    }
    fun clearHourlyTable(){
        dao.clearTable()
    }
}