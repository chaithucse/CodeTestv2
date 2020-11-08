package com.example.weatherreport.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Weather::class), version = 1)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        private var instance: WeatherDatabase? = null
        fun getDatabase(context: Context): WeatherDatabase? {
            if (null == instance) {
                synchronized(WeatherDatabase::class.java) {
                    instance =
                        Room.databaseBuilder(context, WeatherDatabase::class.java, "weather.db")
                            .build()
                }
            }
            return instance
        }
    }
}