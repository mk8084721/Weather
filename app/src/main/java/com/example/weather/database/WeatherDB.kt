package com.example.weather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather.database.model.Favorite
import com.example.weather.database.model.HomeWeather


@Database(entities = [HomeWeather::class, Favorite::class], version = 1 )
abstract class WeatherDB : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDB? = null
        fun getInstance(ctx: Context): WeatherDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, WeatherDB::class.java, "test1"
                )
                    .build()
                INSTANCE = instance
// return instance
                instance
            }
        }
    }
}