package com.example.weatherreport.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.example.weatherreport.WeatherApplication
import com.example.weatherreport.common.AppUtil
import com.example.weatherreport.data.io.ApiClient
import com.example.weatherreport.data.io.ApiService
import com.example.weatherreport.data.model.WeatherResponse
import com.example.weatherreport.data.worker.WeatherWork
import com.google.gson.Gson
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
    private val TAG: String = "WeatherRepository"
    private var weatherData: MutableLiveData<WeatherResponse> = MutableLiveData()

    /**
     * Get weather forecast details from Network and schedule network request for Every 2 hrs on subsequent requests
     */
    fun getWeatherDataFromNetwork(lat: Double, lng: Double): LiveData<WeatherResponse> {
        scheduleNetworkRequest(lat, lng)
        val apiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
        apiService.getWeatherForecast(lat, lng, "metric")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    Log.i(TAG, "onResponse")
                    weatherData.value = response.body()
                    GlobalScope.launch(Dispatchers.IO) {
                        AppUtil.writeJsonToLocal(
                            WeatherApplication.applicationContext(),
                            Gson().toJson(response.body())
                        )
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure")
                    t.printStackTrace()
                }
            })
        return weatherData
    }

    /**
     * Start the Work Manager to schedule the Network requests every 2 hrs
     */
    fun scheduleNetworkRequest(lat: Double, lon: Double) {
        val builder = Data.Builder()
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