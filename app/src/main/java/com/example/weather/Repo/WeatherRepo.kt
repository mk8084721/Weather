package com.example.weather.Repo

import android.content.Context
import com.example.weather.database.LocalDataSource
import com.example.weather.database.model.HomeWeather
import com.example.weather.network.model.CurrentWeather
import com.example.weather.network.model.ForecastWeather
import com.example.weather.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepo(var localDataSource : LocalDataSource , var remoteDataSource:ApiService) {
    suspend fun getCurrentWeather(lon:Float , lat:Float):Flow<CurrentWeather>{
        return flow {
            val currentWeather = remoteDataSource.getCurrentWeather(lon , lat)
            emit(currentWeather)
        }
    }

    fun getWeatherForecast(lon: Float, lat: Float): Flow<ForecastWeather> {
        return flow {
            val forecastWeather = remoteDataSource.getWeatherForecast(lon , lat)
            emit(forecastWeather)
        }
    }
    fun getHomeWeather(): Flow<List<HomeWeather>> {
        return localDataSource.getHomeWeather()
    }

    suspend fun insertHomeWeather(homeWeather: List<HomeWeather>) {
        localDataSource.insertHomeWeather(homeWeather)
    }

    fun updateHomeWeather(homeWeather: List<HomeWeather>) {
        localDataSource.updateHomeWeather(homeWeather)
    }
    fun saveLocationToPreferences(context: Context, latitude: Float, longitude: Float) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Converting double to float
        editor.putFloat("lat", latitude)
        editor.putFloat("lon", longitude)

        editor.apply()  // Apply the changes asynchronously
    }

    fun getLocationFromPreferences(context: Context): Pair<Float, Float> {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)

        // Retrieve the saved string values and convert them back to double
        val latitude = sharedPreferences.getFloat("lat", 0.0f)
        val longitude = sharedPreferences.getFloat("lon", 0.0f)

        return Pair(latitude, longitude)
    }
//    val (latitude, longitude) = getLocationFromPreferences(context)
//    Log.d("Location", "Latitude: $latitude, Longitude: $longitude")


}