package com.example.weatherreport.data.io

import com.example.weatherreport.data.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API service fro end points
 */
interface ApiService {

    @GET("weather")
    fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") metric: String
    ): Call<WeatherResponse>
}