package com.example.weather.Alerts.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.Repo.WeatherRepo
import com.example.weather.database.model.Alert
import com.example.weather.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class AlertsViewModel(var repo : WeatherRepo) : ViewModel() {
    var alertsSF = MutableStateFlow(ApiState())

    fun getAlerts(){
        viewModelScope.launch {
            repo.getAlerts().flowOn(Dispatchers.IO)
                .catch {
                        e-> alertsSF.value = ApiState.Failure(e)
                }
                .collect{
                        values ->
                    alertsSF.value= ApiState.AlertSuccess(values)
                }
        }
    }

    fun insertAlertToDB(alert : Alert){
        viewModelScope.launch(Dispatchers.IO){
            repo.insertAlert(alert)
        }
    }

    fun deleteAlertFromDB(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAlert(alert)
        }
    }
}
