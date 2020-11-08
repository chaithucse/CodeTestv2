package com.example.weatherreport

import androidx.test.InstrumentationRegistry
import com.example.weatherreport.data.local.Weather
import com.example.weatherreport.data.local.WeatherDao
import com.example.weatherreport.data.local.WeatherDatabase
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class LocalDatabaseTest {

    private var weatherDao: WeatherDao? = null

    @Before
    fun setup() {
        weatherDao =
            WeatherDatabase.getDatabase(InstrumentationRegistry.getTargetContext())?.weatherDao()
    }

    @Test
    fun checkDatabaseInsertion() {
        var weather = Weather().apply {
            city = "Bridgewater"
        }
        weatherDao?.insertToLocal(weather)
        val data = weatherDao?.getWeatherDataByID("Bridgewater")
        assertEquals(weather.city, data?.city)
    }
}