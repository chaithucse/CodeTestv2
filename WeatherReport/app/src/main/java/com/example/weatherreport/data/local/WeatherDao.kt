package com.example.weatherreport.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {

    @Query("SELECT * from weather")
    fun getWeatherData(): LiveData<Weather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToLocal(weather: Weather)

    @Query("SELECT * from weather WHERE city = :city")
    fun getWeatherDataByID(city: String) : Weather
}