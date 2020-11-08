package com.example.weatherreport.data.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherreport.data.io.ApiClient
import com.example.weatherreport.data.io.ApiService
import com.example.weatherreport.data.local.Weather
import com.example.weatherreport.data.local.WeatherDatabase
import com.example.weatherreport.data.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Worker class for performing background tasks
 */
class WeatherWork(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val latitude = inputData.getDouble("latitude", 0.000)
        val longitude = inputData.getDouble("longitude", 0.000)

        val apiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
        apiService.getWeatherForecast(latitude, longitude, "metric")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val model = Weather().apply {
                            city = response.body()!!.name
                            tempHigh = response.body()!!.main.temp_max.toString()
                            tempLow = response.body()!!.main.temp_min.toString()
                            temp = response.body()!!.main.temp.toString()
                            sunrise = response.body()!!.sys.sunrise.toString()
                            sunset = response.body()!!.sys.sunset.toString()
                            humidity = response.body()!!.main.humidity.toString()
                            country = response.body()!!.sys.country.toString()
                            feesLike = response.body()!!.main.feels_like.toString()
                            weadesc = response.body()!!.weather[0].description
                        }
                        WeatherDatabase.getDatabase(applicationContext)!!.weatherDao()!!
                            .insertToLocal(model)
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    t.printStackTrace()
                }

            })
        return Result.success()
    }
}