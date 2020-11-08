package com.example.weatherreport.data.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherreport.WeatherApplication
import com.example.weatherreport.common.AppUtil
import com.example.weatherreport.data.io.ApiClient
import com.example.weatherreport.data.io.ApiService
import com.example.weatherreport.data.model.WeatherResponse
import com.google.gson.Gson
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
                        AppUtil.writeJsonToLocal(
                            WeatherApplication.applicationContext(),
                            Gson().toJson(response.body())
                        )
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        return Result.success()
    }
}