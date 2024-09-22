package com.example.weather.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.Repo.WeatherRepo
import com.example.weather.model.CurrentWeather
import com.example.weather.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class HomeViewModel(var repo :WeatherRepo) : ViewModel(){

    var currentWeatherSF = MutableStateFlow(ApiState())
    var weatherForecastSF = MutableStateFlow(ApiState())

    fun getCurrentWeather(lon:Double , lat:Double){
        viewModelScope.launch {
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

}
