package com.example.weather.Alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.Repo.WeatherRepo

class AlertViewModelFactory (private val _repo : WeatherRepo) :
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlertsViewModel::class.java)){
            AlertsViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("VM class not found")
        }
    }
}
