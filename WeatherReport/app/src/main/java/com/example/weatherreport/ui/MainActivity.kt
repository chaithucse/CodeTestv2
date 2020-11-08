package com.example.weatherreport.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.weatherreport.common.AppUtil
import com.example.weatherreport.data.model.WeatherResponse
import com.example.weatherreport.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.gson.Gson

/**
 * Launcher activity
 */
class MainActivity : AppCompatActivity() {
    private val TAG: String = "MainActivity"
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val PERMISSION_LOCATION_REQUEST_CODE = 100
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
     * Check the location permission and request for latitude and longitude values
     */
    private fun checkLocationInformation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            getDeviceLocationCoordinates()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Permission Access")
                    .setMessage("Location Permission access needed to show weather data")
                    .setPositiveButton(
                        "Ok",
                        DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSION_LOCATION_REQUEST_CODE
                            )
                        })
                    .create().show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_LOCATION_REQUEST_CODE
                )
            }
        }
    }

    /**
     * Get device location lat & long coordinates if location permission accepted by the user
     */
    private fun getDeviceLocationCoordinates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
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
                            getWeatherData()
                        }
                    }
                    fusedLocationClient?.requestLocationUpdates(locationReq, calback, null)
                } else {
                    latitude = location.latitude.toDouble()
                    longitude = location.longitude.toDouble()
                    getWeatherData()
                }
            }
        }
    }

    /**
     * Retrieve the data from app storage if available otherwise get latest data from Network
     *
     */
    @SuppressLint("SetTextI18n")
    fun getWeatherData() {
        viewModel = ViewModelProviders.of(this).get(WeatherVIewModel::class.java)
        if (AppUtil.isLocalDataAvailable()) {
            val json = AppUtil.readDataFromLocal()
            val weather = Gson().fromJson(json, WeatherResponse::class.java)
            displayUI(weather)
            return
        }
        viewModel.getWeatherDataFromNetwork(latitude, longitude).observe(this, Observer {
            displayUI(it)
        })
    }

    /**
     * Display weather data on UI
     */
    private fun displayUI(weather: WeatherResponse) {
        binding.temparature.text = weather.main.temp.toString() + "\u00B0"
        binding.tempHigh.text = weather.main.temp_max.toString() + "\u00B0"
        binding.tempLow.text = weather.main.temp_min.toString() + "\u00B0"
        binding.feeslike.text = "Feels like: " + weather.main.feels_like.toString() + "\u00B0"
        binding.descWeath.text = weather.weather[0].description
        binding.city.text = weather.name + " ," + weather.sys.country
        binding.sunrise.text = AppUtil.timeConverter(weather.sys.sunrise.toLong())
        binding.sunset.text = AppUtil.timeConverter(weather.sys.sunset.toLong())
        binding.humidity.text = weather.main.humidity.toString()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_LOCATION_REQUEST_CODE -> {
                for (grantResult in grantResults) {
                    if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getDeviceLocationCoordinates()
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            Toast.makeText(
                                this,
                                "Permission denied by user. Go to App info page under Settings and accept permission",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}