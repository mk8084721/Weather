package com.example.weather.Alerts.view

import com.example.weather.database.model.Alert

interface ICommunicate {
    fun deleteAlert(alert:Alert)
}