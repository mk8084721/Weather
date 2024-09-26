package com.example.weather.Favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.Repo.WeatherRepo

class FavoriteViewModelFactory (private val _repo : WeatherRepo) :
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            FavoriteViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("VM class not found")
        }
    }
}