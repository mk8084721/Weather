package com.example.weather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather.database.model.Alert
import com.example.weather.database.model.Favorite
import com.example.weather.database.model.HomeWeather
import com.example.weather.database.model.Hourly


@Database(entities = [HomeWeather::class, Favorite::class , Alert::class , Hourly::class], version = 1 )
abstract class WeatherDB : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDB? = null
        fun getInstance(ctx: Context): WeatherDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, WeatherDB::class.java, "test3"
                )
                    .build()
                INSTANCE = instance
// return instance
                instance
            }
        }
    }
}