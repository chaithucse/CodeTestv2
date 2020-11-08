package com.example.weatherreport.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.example.weatherreport.WeatherApplication
import com.example.weatherreport.data.io.ApiClient
import com.example.weatherreport.data.io.ApiService
import com.example.weatherreport.data.local.Weather
import com.example.weatherreport.data.local.WeatherDao
import com.example.weatherreport.data.local.WeatherDatabase
import com.example.weatherreport.data.model.WeatherResponse
import com.example.weatherreport.data.worker.WeatherWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

/**
 * Repository for application backend data(remote or local)
 */
class WeatherRepository {
    private var weatherDao: WeatherDao
    private var weatherData: MutableLiveData<WeatherResponse> = MutableLiveData()

    init {
        weatherDao =
            WeatherDatabase.getDatabase(WeatherApplication.applicationContext())!!.weatherDao()
    }

    fun getWeatherForecast(
        fetch: Boolean,
        latitude: Double?,
        longitude: Double?
    ): LiveData<Weather> {
        return weatherDao.getWeatherData()
    }

    /**
     * Get weather forecast details
     *
     * @TODO need to change this logic
     */
    fun getWeatherDataFromNetwork(lat: Double, lng: Double): LiveData<WeatherResponse> {
        scheduleNetwotkRequest(lat!!, lng!!)
        val apiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
        apiService.getWeatherForecast(lat, lng, "metric")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    weatherData.value = response.body()
                    GlobalScope.launch(Dispatchers.IO) {
                        val model = Weather().apply {
                            city = response.body()!!.name
                            tempHigh = response.body()!!.main.temp_max.toString()
                            tempLow = response.body()!!.main.temp_min.toString()
                            temp = response.body()!!.main.temp.toString()
                            sunrise = response.body()!!.sys.sunrise.toString()
                            sunset = response.body()!!.sys.sunset.toString()
                            humidity = response.body()!!.main.humidity.toString()
                            feesLike = response.body()!!.main.feels_like.toString()
                            weadesc = response.body()!!.weather[0].description
                            country = response.body()!!.sys.country
                        }
                        weatherDao.insertToLocal(model)
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        return weatherData
    }

    /**
     * Insert remote into DB
     */
    fun scheduleNetwotkRequest(lat: Double, lon: Double) {
        var builder = Data.Builder()
            .putDouble("latitude", lat)
            .putDouble("longitude", lon)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val work = PeriodicWorkRequestBuilder<WeatherWork>(2, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInputData(builder)
            .build()
        val workManager = WorkManager.getInstance(WeatherApplication.applicationContext())
        workManager.enqueue(work)
    }
}