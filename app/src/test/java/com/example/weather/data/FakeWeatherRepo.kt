package com.example.weather.data

import android.content.Context
import com.example.weather.Repo.IWeatherRepo
import com.example.weather.database.model.Alert
import com.example.weather.database.model.Favorite
import com.example.weather.database.model.HomeWeather
import com.example.weather.database.model.Hourly
import com.example.weather.network.model.Clouds
import com.example.weather.network.model.Coord
import com.example.weather.network.model.CurrentWeather
import com.example.weather.network.model.ForecastWeather
import com.example.weather.network.model.Temp
import com.example.weather.network.model.WeatherStatus
import com.example.weather.network.model.Wind
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeWeatherRepo() : IWeatherRepo {
    var shouldReturnError = false

    // Stubbed current weather data
    private val currentWeatherStub = CurrentWeather(Coord(0.0f,0.0f), arrayOf(WeatherStatus(1,"","","")),"",
        Temp(0.0f,0.0f,0.0f,0.0f,0,0,0,0),
        0,
        Wind(0.0f,0,0.0f),
        0,0,"",0,"",
        Clouds(0)
    )
    private val forecastWeatherStub = ForecastWeather(mutableListOf<CurrentWeather>().toTypedArray())// Replace with actual weather forecast model
    private val homeWeatherList = mutableListOf<HomeWeather>()
    private val hourlyWeatherList = mutableListOf<Hourly>()

    val fakeFavorites = listOf(
        Favorite(0.0 , 0.0 , "Location1"),
        Favorite(0.0 , 0.0 ,"Location2")
    )
    val insertedFavorites = mutableListOf<Favorite>()
    val deletedFavorites = mutableListOf<Favorite>()
    var savedLatitude: Float? = null
    var savedLongitude: Float? = null
    override suspend fun getCurrentWeather(lon: Float, lat: Float): Flow<CurrentWeather> = flow {
        if (shouldReturnError) throw RuntimeException("Error fetching current weather")
        emit(currentWeatherStub)
    }

    override fun getWeatherForecast(lon: Float, lat: Float): Flow<ForecastWeather> = flow {
        if (shouldReturnError) throw RuntimeException("Error fetching weather forecast")
        emit(forecastWeatherStub)
    }

    override fun getHomeWeather(): Flow<List<HomeWeather>> = flow {
        emit(homeWeatherList)
    }

    override suspend fun insertHomeWeather(homeWeather: List<HomeWeather>) {
        homeWeatherList.addAll(homeWeather)
    }

    override fun updateHomeWeather(homeWeather: List<HomeWeather>)  {
        homeWeatherList.clear()
        homeWeatherList.addAll(homeWeather)
    }

    override fun getAllFavWeather(): Flow<List<Favorite>> {
        return flow {
            emit(fakeFavorites)
        }
    }

    override fun insertFavWeather(favorites: List<Favorite>) {
        insertedFavorites.addAll(favorites)
    }

    override fun deleteFavWeather(favorites: List<Favorite>) {
        deletedFavorites.addAll(favorites)
    }

    override fun saveLocationToPreferences(context: Context, latitude: Float, longitude: Float) {
        savedLatitude = latitude
        savedLongitude = longitude
    }

    override fun getLocationFromPreferences(context: Context): Pair<Float, Float> {
        return Pair(savedLatitude ?: 0.0f, savedLongitude ?: 0.0f)
    }

    override fun getAlerts(): Flow<List<Alert>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: Alert) {
        TODO("Not yet implemented")
    }

    override fun clearHourlyTable() {
        hourlyWeatherList.clear()
    }

    override fun insertHourlyWeather(hourlyWeather: MutableList<Hourly>) {
        hourlyWeatherList.addAll(hourlyWeather)
    }

    override fun getHourlyWeather(): Flow<List<Hourly>>  = flow {
        emit(hourlyWeatherList)
    }

    override fun insertDefaultSettings(context: Context) {
        TODO("Not yet implemented")
    }

    override fun getDefaultSettings(context: Context): List<String?> {
        TODO("Not yet implemented")
    }

    override fun getLangFromPreferences(context: Context): String? {
        TODO("Not yet implemented")
    }

    override fun deleteAlert(alert: Alert) {
        TODO("Not yet implemented")
    }
}