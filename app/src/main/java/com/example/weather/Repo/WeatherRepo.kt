package com.example.weather.Repo

import android.content.Context
import com.example.weather.database.ILocalDataSource
import com.example.weather.database.LocalDataSource
import com.example.weather.database.model.Alert
import com.example.weather.database.model.Favorite
import com.example.weather.database.model.HomeWeather
import com.example.weather.database.model.Hourly
import com.example.weather.network.model.CurrentWeather
import com.example.weather.network.model.ForecastWeather
import com.example.weather.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepo( var localDataSource : ILocalDataSource,  var remoteDataSource:ApiService) :
    IWeatherRepo {
    override suspend fun getCurrentWeather(lon:Float, lat:Float):Flow<CurrentWeather>{
        return flow {
            val currentWeather = remoteDataSource.getCurrentWeather(lon , lat)
            emit(currentWeather)
        }
    }

    override fun getWeatherForecast(lon: Float, lat: Float): Flow<ForecastWeather> {
        return flow {
            val forecastWeather = remoteDataSource.getWeatherForecast(lon , lat)
            emit(forecastWeather)
        }
    }

    //// Home ////////////////

    override fun getHomeWeather(): Flow<List<HomeWeather>> {
        return localDataSource.getHomeWeather()
    }

    override suspend fun insertHomeWeather(homeWeather: List<HomeWeather>) {
        localDataSource.insertHomeWeather(homeWeather)
    }

    override fun updateHomeWeather(homeWeather: List<HomeWeather>) {
        localDataSource.updateHomeWeather(homeWeather)
    }

    //// Favorite ////////////////

    override fun getAllFavWeather(): Flow<List<Favorite>> {
        return localDataSource.getAllFavWeather()
    }
    override fun insertFavWeather(list: List<Favorite>) {
        localDataSource.insertFavWeather(list)
    }
    override fun deleteFavWeather(list: List<Favorite>) {
        localDataSource.deleteFavWeather(list)
    }

    //// SHP /////////////////////

    override fun insertDefaultSettings(context: Context) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("lang", "en")
        editor.putString("speed", "m/s")
        editor.putString("unit", "c")
        editor.apply()  // Apply the changes asynchronously
    }
    override fun getDefaultSettings(context: Context): List<String?> {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val lang = sharedPreferences.getString("lang","en")
        val speed = sharedPreferences.getString("speed", "m/s")
        val unit = sharedPreferences.getString("unit", "c")
        return listOf(lang,speed,unit)
    }

    fun saveLangToPreferences(context: Context, lang: String) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("lang", lang)
        editor.apply()
    }

    override fun getLangFromPreferences(context: Context):String? {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val lang = sharedPreferences.getString("lang", "en")
        return lang
    }

    fun saveSpeedToPreferences(context: Context, speed: String) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("speed", speed)
        editor.apply()  // Apply the changes asynchronously
    }

    fun getSpeedFromPreferences(context: Context):String? {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val speed = sharedPreferences.getString("speed", "m/s")
        return speed
    }

    fun saveUnitToPreferences(context: Context, unit: String) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("unit", unit)
        editor.apply()  // Apply the changes asynchronously
    }

    fun getUnitFromPreferences(context: Context):String? {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val unit = sharedPreferences.getString("unit", "c")
        return unit
    }

    override fun saveLocationToPreferences(context: Context, latitude: Float, longitude: Float) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Converting double to float
        editor.putFloat("lat", latitude)
        editor.putFloat("lon", longitude)
        editor.apply()  // Apply the changes asynchronously
    }

    override fun getLocationFromPreferences(context: Context): Pair<Float, Float> {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)

        // Retrieve the saved string values and convert them back to double
        val latitude = sharedPreferences.getFloat("lat", 0.0f)
        val longitude = sharedPreferences.getFloat("lon", 0.0f)

        return Pair(latitude, longitude)
    }

    override fun getAlerts() :Flow<List<Alert>>{
        return localDataSource.getAlerts()
    }

    override suspend fun insertAlert(alert: Alert) {
        localDataSource.insertAlert(alert)
    }

    override fun clearHourlyTable() {
        localDataSource.clearHourlyTable()
    }

    override fun insertHourlyWeather(hourlyWeather: MutableList<Hourly>) {
        localDataSource.insertHourlyWeather(hourlyWeather)
    }

    override fun getHourlyWeather(): Flow<List<Hourly>> {
        return localDataSource.getHourlyWeather()
    }


//    val (latitude, longitude) = getLocationFromPreferences(context)
//    Log.d("Location", "Latitude: $latitude, Longitude: $longitude")


}