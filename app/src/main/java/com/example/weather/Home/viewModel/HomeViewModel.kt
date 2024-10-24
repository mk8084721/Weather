package com.example.weather.Home.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.Repo.IWeatherRepo
import com.example.weather.Repo.WeatherRepo
import com.example.weather.database.model.HomeWeather
import com.example.weather.database.model.Hourly
import com.example.weather.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class HomeViewModel(var repo :IWeatherRepo) : ViewModel(){

    var currentWeatherSF = MutableStateFlow(ApiState())
    var weatherForecastSF = MutableStateFlow(ApiState())
    var homeWeatherSF = MutableStateFlow<List<HomeWeather>>(emptyList())
    var hourlyWeatherSF = MutableStateFlow(ApiState())

    fun getCurrentWeather(lon:Float , lat:Float){
        viewModelScope.launch {
            currentWeatherSF.value = ApiState.Loading
            // Calling Current Weather
            repo.getCurrentWeather(lon , lat).flowOn(Dispatchers.IO)
                .catch {
                        e-> currentWeatherSF.value = ApiState.Failure(e)
                }
                .collect{
                        values ->
                    Log.i("TAG", "getCurrentWeather: $values")
                    currentWeatherSF.value= ApiState.Success(values)
                }
        }
        viewModelScope.launch {
            weatherForecastSF.value = ApiState.Loading
            //Calling Forcast or Hourly Weather
            repo.getWeatherForecast(lon , lat).flowOn(Dispatchers.IO)
                .catch {
                        e-> weatherForecastSF.value = ApiState.Failure(e)
                }
                .collect{
                        values ->
                    Log.i("TAG", "getWeatherForecast: $values")
                    weatherForecastSF.value= ApiState.ForecastSuccess(values)
                }
        }
    }

    fun getHomeWeather() {
        viewModelScope.launch {
            repo.getHomeWeather().flowOn(Dispatchers.IO)
                .collect { values ->
                    Log.i("TAG", "getWeatherForecast: $values")
                    homeWeatherSF.value = values
                }
        }
    }


    fun insertEmptyHomeWeather(homeWeather: HomeWeather) {
        viewModelScope.launch(Dispatchers.IO){
            val list = listOf(homeWeather)
            repo.insertHomeWeather(list)
        }
    }

    fun updateHomeWeather(homeWeather: HomeWeather) {
        viewModelScope.launch(Dispatchers.IO){
            val list = listOf(homeWeather)
            repo.updateHomeWeather(list)
        }
    }
    fun saveLocationSHP(context :Context , lon: Float , lat: Float){
        repo.saveLocationToPreferences(context , lat , lon)
    }
    fun getLocationSHP(context: Context): Pair<Float, Float> {
        return repo.getLocationFromPreferences(context)
    }

    fun clearHourlyTable() {
        viewModelScope.launch(Dispatchers.IO){
            repo.clearHourlyTable()
        }
    }

    fun insertHourlyWeather(hourlyWeather: MutableList<Hourly>) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertHourlyWeather(hourlyWeather)
        }
    }

    fun getHourlyWeather() {
        viewModelScope.launch(Dispatchers.IO){
            hourlyWeatherSF.value = ApiState.Loading
            // Calling Current Weather
            repo.getHourlyWeather().flowOn(Dispatchers.IO)
                .catch {
                        e-> currentWeatherSF.value = ApiState.Failure(e)
                }
                .collect{
                        values ->
                    Log.i("TAG", "getCurrentWeather: $values")
                    hourlyWeatherSF.value= ApiState.LocalHourlySuccess(values)
                }
        }
    }

    fun insertDefaultSettings(context: Context) {
        repo.insertDefaultSettings(context)
    }

    fun getLangSHP(context: Context): String? {
        return repo.getLangFromPreferences(context)
    }

}
