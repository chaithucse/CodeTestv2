package com.example.weatherreport.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.weatherreport.data.WeatherRepository
import com.example.weatherreport.data.local.Weather
import com.example.weatherreport.data.model.WeatherResponse

class WeatherVIewModel : ViewModel() {
    private var repository: WeatherRepository = WeatherRepository()

    fun getWeatherForecast(
        fetch: Boolean,
        latitude: Double?,
        longitude: Double?
    ): LiveData<Weather> {
        return repository.getWeatherForecast(fetch, latitude, longitude)
    }

    fun getWeatherDataFromNetwork(
        latitude: Double?,
        longitude: Double?
    ): LiveData<WeatherResponse> {
        return repository.getWeatherDataFromNetwork(latitude!!, longitude!!)
    }
}