package com.example.weatherreport.common

import android.content.Context
import com.example.weatherreport.WeatherApplication
import java.io.*
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat

/**
 * Utility class
 */
class AppUtil {

    companion object {

        /**
         * Time converter to specific time
         */
        fun timeConverter(timestamp: Long): String {
            val sdf = SimpleDateFormat("hh:mm a")
            val date = java.util.Date(timestamp * 1000)
            return sdf.format(date);
        }

        /**
         * Write the REST API response to application storage(app specific storage area)
         */
        fun writeJsonToLocal(context: Context, data: String) {
            val file = File(context.filesDir, "weather.json")
            val fileWriter = FileWriter(file)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.write(data)
            bufferedWriter.close()
        }

        /**
         * check offline data available or not
         */
        fun isLocalDataAvailable(): Boolean {
            val file: File =
                WeatherApplication.applicationContext().getFileStreamPath("weather.json")
            return file.exists()
        }

        /**
         * Read data from local storage for offline access
         */
        fun readDataFromLocal(): String {
            val fis: FileInputStream =
                WeatherApplication.applicationContext().openFileInput("weather.json")
            val inputStreamReader = InputStreamReader(fis, StandardCharsets.UTF_8)
            val stringBuilder = StringBuilder()
            try {
                BufferedReader(inputStreamReader).use { reader ->
                    var line = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line).append('\n')
                        line = reader.readLine()
                    }
                }
            } catch (e: IOException) {
                // Error occurred when opening raw file for reading.
            }
            return stringBuilder.toString()
        }
    }
}