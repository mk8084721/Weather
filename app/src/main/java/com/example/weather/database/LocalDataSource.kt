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

class LocalDataSource(override var context: Context) : ILocalDataSource {
    var dao = WeatherDB.getInstance(context).getWeatherDao()

    override fun getHomeWeather(): Flow<List<HomeWeather>> {
        return dao.getHomeWeather()
    }

    override suspend fun insertHomeWeather(homeWeather: List<HomeWeather>) {
        dao.insertHomeWeather(homeWeather)
    }

    override fun updateHomeWeather(homeWeather: List<HomeWeather>) {
        dao.updateHomeWeather(homeWeather)
    }

    override fun deleteHomeWeather(homeWeather: List<HomeWeather>) {
        dao.deleteHomeWeather(homeWeather)
    }

    override fun getAllFavWeather(): Flow<List<Favorite>> {
        return dao.getAllFavWeather()
    }

    override fun insertFavWeather(favWeather: List<Favorite>) {
        dao.insertFavWeather(favWeather)
    }

    override fun updateFavWeather(favWeather: List<Favorite>) {
        dao.updateFavWeather(favWeather)
    }

    override fun deleteFavWeather(favWeather: List<Favorite>) {
        dao.deleteFavWeather(favWeather)
    }

    override fun getAlerts(): Flow<List<Alert>> {
        return dao.getAllAlerts()
    }

    override suspend fun insertAlert(alert: Alert) {
        dao.insertAlert(alert)
    }

    //Hourly
    override fun getHourlyWeather() : Flow<List<Hourly>>{
        return dao.getHourlyWeather()
    }
    override fun insertHourlyWeather(hourlyWeather : List<Hourly>){
        dao.insertHourlyWeather(hourlyWeather)
    }
    override fun updateHourlyWeather(hourlyWeather : List<Hourly>){
        dao.updateHourlyWeather(hourlyWeather)
    }
    override fun deleteHourlyWeather(hourlyWeather : List<Hourly>){
        dao.deleteHourlyWeather(hourlyWeather)
    }
    override fun clearHourlyTable(){
        dao.clearTable()
    }
}