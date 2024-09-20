package com.example.weather.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.Repo.WeatherRepo
import com.example.weather.model.CurrentWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class HomeViewModel(var repo :WeatherRepo) : ViewModel(){
    private var _currentWeatherList = MutableLiveData<CurrentWeather>() //Observable -> emit
    var currentWeatherList: LiveData<CurrentWeather> = _currentWeatherList //Observable

    fun getCurrentWeather(lat:Double , lon:Double){
        //1. get list od persons
        viewModelScope.launch {
            val postList = repo.getCurrentWeather(lat, lon)
                .flowOn(Dispatchers.IO)
                .collect{
                        values ->_currentWeatherList.postValue(values)
                    //repo.insertCurrentWeather(values)
                }
            /*val products = repo.getAllLocalProducts().flowOn(Dispatchers.IO)
                .collect{
                        values ->
                    _productsList.postValue(values)
                }*/

            //val mk = repo.getAllRemoteProducts().single()
            /*withContext(Dispatchers.Main) {
                    repo.insertProducts(postList.products.toList())
                    val products = repo.getAllLocalProducts()
                    _productsList.postValue(products)
            }*/
        }
    }
}
