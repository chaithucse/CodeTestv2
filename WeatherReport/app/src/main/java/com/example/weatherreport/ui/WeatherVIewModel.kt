package com.example.weatherreport.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.weatherreport.data.WeatherRepository
import com.example.weatherreport.data.model.WeatherResponse

/**
 * View Model class will hold the data to send to UI with help of Livedata observer
 */
class WeatherVIewModel : ViewModel() {
    private var repository: WeatherRepository = WeatherRepository()

    /**
     * Get Weather data from network
     */
    fun getWeatherDataFromNetwork(
        latitude: Double?,
        longitude: Double?
    ): LiveData<WeatherResponse> {
        return repository.getWeatherDataFromNetwork(latitude!!, longitude!!)
    }
}