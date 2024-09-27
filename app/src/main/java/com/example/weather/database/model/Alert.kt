package com.example.weather.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title : String,
    val timeInMillis: Long
)
