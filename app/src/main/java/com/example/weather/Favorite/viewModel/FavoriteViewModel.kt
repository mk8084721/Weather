package com.example.weather.Favorite.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.Repo.IWeatherRepo
import com.example.weather.database.model.Favorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class FavoriteViewModel(var repo : IWeatherRepo) : ViewModel(){
    var allFavoriteWeatherSF = MutableStateFlow<List<Favorite>>(emptyList())

    fun getAllFavWeather(){
        viewModelScope.launch {
            repo.getAllFavWeather().flowOn(Dispatchers.IO)
                .collect { values ->
                    Log.i("TAG", "getWeatherForecast: $values")
                    allFavoriteWeatherSF.value = values
                }
        }
    }
    fun insertFavWeather(favoriteWeather: Favorite) {
        viewModelScope.launch(Dispatchers.IO){
            val list = listOf(favoriteWeather)
            repo.insertFavWeather(list)
        }
    }
    fun deleteFavWeather(favoriteWeather: Favorite) {
        viewModelScope.launch(Dispatchers.IO){
            val list = listOf(favoriteWeather)
            repo.deleteFavWeather(list)
        }
    }
}