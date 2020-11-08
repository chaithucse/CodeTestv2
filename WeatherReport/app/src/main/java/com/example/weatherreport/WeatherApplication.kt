package com.example.weatherreport

import android.app.Application
import android.content.Context

class WeatherApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: WeatherApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = WeatherApplication.applicationContext()
    }
}