package com.example.weather.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weather.database.model.Alert
import com.example.weather.database.model.Favorite
import com.example.weather.database.model.HomeWeather
import com.example.weather.database.model.Hourly
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    // Home Waether
    @Query("SELECT * FROM HomeWeather")
    fun getHomeWeather() : Flow<List<HomeWeather>>
    @Insert(onConflict = OnConflictStrategy.IGNORE , entity = HomeWeather::class)
    suspend fun insertHomeWeather(homeWeather : List<HomeWeather>)
    @Update(entity = HomeWeather::class)
    fun updateHomeWeather(homeWeather : List<HomeWeather>)
    @Delete(entity = HomeWeather::class)
    fun deleteHomeWeather(homeWeather : List<HomeWeather>)

    // Favorite
    @Query("SELECT * FROM Favorite")
    fun getAllFavWeather() : Flow<List<Favorite>>
    @Insert(onConflict = OnConflictStrategy.IGNORE , entity = Favorite::class)
    fun insertFavWeather(favWeather : List<Favorite>)
    @Update(entity = Favorite::class)
    fun updateFavWeather(favWeather : List<Favorite>)
    @Delete(entity = Favorite::class)
    fun deleteFavWeather(favWeather : List<Favorite>)

    //Alerts
    @Insert(entity = Alert::class)
    suspend fun insertAlert(alert: Alert)

    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): Flow<List<Alert>>

    // Hourly
    @Query("SELECT * FROM Hourly")
    fun getHourlyWeather() : Flow<List<Hourly>>
    @Insert(onConflict = OnConflictStrategy.IGNORE , entity = Hourly::class)
    fun insertHourlyWeather(favWeather : List<Hourly>)
    @Update(entity = Hourly::class)
    fun updateHourlyWeather(favWeather : List<Hourly>)
    @Delete(entity = Hourly::class)
    fun deleteHourlyWeather(favWeather : List<Hourly>)
    @Query("DELETE FROM Hourly")
    fun clearTable()


}