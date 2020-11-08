package com.example.weatherreport.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class Weather(
    @PrimaryKey var city: String = "",
    var country: String = "",
    var long: Double = 0.000,
    var latt: Double = 0.000,
    var temp: String = "",
    var tempHigh: String = "",
    var tempLow: String = "",
    var sunrise: String = "",
    var sunset: String = "",
    var humidity: String = "",
    var feesLike: String = "",
    var weadesc: String = ""
)