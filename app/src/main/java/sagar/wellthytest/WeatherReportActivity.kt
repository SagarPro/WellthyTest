package sagar.wellthytest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import sagar.wellthytest.utils.LocaleHelper.setLocale
import sagar.wellthytest.utils.PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE
import sagar.wellthytest.utils.PermissionUtils.getCurrentCity
import sagar.wellthytest.utils.PermissionUtils.isAccessFineLocationGranted
import sagar.wellthytest.utils.PermissionUtils.isLocationEnabled
import sagar.wellthytest.utils.PermissionUtils.requestAccessFineLocationPermission
import sagar.wellthytest.utils.PermissionUtils.showGPSNotEnabledDialog
import java.util.*


class WeatherReportActivity : AppCompatActivity() {

    private val TAG = "WeatherReportActivity"

    private var currentLatLng: LatLng? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var tvCityName: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvWeatherDescription: TextView
    private lateinit var ivWeatherImage: ImageView

    private lateinit var swLanguage: SwitchCompat
    private lateinit var swTheme: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_report)

        tvCityName = findViewById(R.id.tvCityName)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvWeatherDescription = findViewById(R.id.tvWeatherDescription)
        ivWeatherImage = findViewById(R.id.ivWeatherImage)

        swLanguage = findViewById(R.id.swLanguage)
        swTheme = findViewById(R.id.swTheme)

        setupLanguage()
        setupTheme()
    }

    override fun onStart() {
        super.onStart()
        fetchLocation()
    }

    private fun fetchLocation(){
        //if currentLatLng is null check for location permission and get latLng
        //if currentLatLng is not null then no need to do anything
        if (currentLatLng == null) {
            when {
                this.isAccessFineLocationGranted() -> {
                    when {
                        this.isLocationEnabled() -> {
                            setUpLocationListener()
                        }
                        else -> {
                            this.showGPSNotEnabledDialog()
                        }
                    }
                }
                else -> {
                    this.requestAccessFineLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
                }
            }
        }
    }

    //function to get last known location if present else get new location
    private fun setUpLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestAccessFineLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val location = task.result
            if (location == null){
                //get new location, if last known location is null
                getNewLocation()
            } else {
                onLocationReceived(location)
            }
        }
    }

    //function to get new location
    private fun getNewLocation(){
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val location = p0.lastLocation
                onLocationReceived(location)
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestAccessFineLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()!!
        )
    }

    //once current location is received get city name
    private fun onLocationReceived(location: Location){
        Log.d(TAG, "${location.latitude} - ${location.longitude}")
        currentLatLng = LatLng(location.latitude, location.longitude)
        val city = baseContext.getCurrentCity(currentLatLng!!)
        Log.d(TAG, "City: $city")

        tvCityName.text = city
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    //setUpLocationListener() //if location permission is given, get current location
                    fetchLocation()
                } else {
                    //display location permission denied msg
                }
            }
        }
    }

    //function to init and change language
    private fun setupLanguage(){

        var currentLanguage = "english"

        swLanguage.isChecked = currentLanguage != "english"
        //setLocale("hi-rIN")

        swLanguage.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                currentLanguage = "hindi"
                setLocale("hi-rIN")
            } else {
                currentLanguage = "english"
            }
        }

    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
        //resources.updateConfiguration(config, resources.displayMetrics)

        //finish()
        //startActivity(getIntent())

        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    //function to init and change theme
    private fun setupTheme(){

        var currentTheme = "light"

        swTheme.isChecked = currentTheme != "light"

        swTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                currentTheme = "dark"
                changeTheme(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                currentTheme = "light"
                changeTheme(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

    }

    private fun changeTheme(mode: Int){
        AppCompatDelegate.setDefaultNightMode(mode)
    }

}