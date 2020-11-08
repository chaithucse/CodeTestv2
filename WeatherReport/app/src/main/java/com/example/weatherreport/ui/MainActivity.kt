package com.example.weatherreport.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.weatherreport.common.AppUtil
import com.example.weatherreport.databinding.ActivityMainBinding
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {
    private val TAG: String = "MainActivity"
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var viewModel: WeatherVIewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationInformation()
    }

    /**
     * Check the location permission and request for latitdue and langitude values
     */
    private fun checkLocationInformation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                if (location == null) {
                    val locationReq = LocationRequest.create()
                    locationReq.interval = 5 * 1000
                    locationReq.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    val calback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult?) {
                            super.onLocationResult(result)
                            val location = result?.locations?.getOrNull(0)
                            latitude = location?.latitude
                            longitude = location?.longitude
                            displayUI()
                        }
                    }
                    fusedLocationClient?.requestLocationUpdates(locationReq, calback, null)
                } else {
                    latitude = location.latitude.toDouble()
                    longitude = location.longitude.toDouble()
                    displayUI()
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    /**
     * Display UI
     *
     * @TODO need to fix this logic
     */
    @SuppressLint("SetTextI18n")
    fun displayUI() {
        viewModel = ViewModelProviders.of(this).get(WeatherVIewModel::class.java)
        viewModel.getWeatherDataFromNetwork(latitude, longitude).observe(this, Observer {
            binding.temparature.text = it.main.temp.toString() + "\u00B0"
            binding.tempHigh.text = it.main.temp_max.toString() + "\u00B0"
            binding.tempLow.text = it.main.temp_min.toString() + "\u00B0"
            binding.feeslike.text = "Feels like: " + it.main.feels_like.toString() + "\u00B0"
            binding.descWeath.text = it.weather[0].description
            binding.city.text = it.name + " ," + it.sys.country
            binding.sunrise.text = AppUtil.timeConverter(it.sys.sunrise.toLong())
            binding.sunset.text = AppUtil.timeConverter(it.sys.sunset.toLong())
            binding.humidity.text = it.main.humidity.toString()
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                for (grantResult in grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        Log.d(TAG, "Permission denied")
                    } else {
                        checkLocationInformation()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}