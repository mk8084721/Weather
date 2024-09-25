package com.example.weather.Home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.Repo.WeatherRepo

class HomeViewModelFactory (private val _repo : WeatherRepo) :
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("VM class not found")
        }
    }
}