package sagar.wellthytest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
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
import sagar.wellthytest.utils.AppConstants.LANGUAGE_ENGLISH
import sagar.wellthytest.utils.AppConstants.LANGUAGE_HINDI
import sagar.wellthytest.utils.AppConstants.SELECTED_LANGUAGE
import sagar.wellthytest.utils.AppConstants.SELECTED_THEME
import sagar.wellthytest.utils.AppConstants.THEME_DARK
import sagar.wellthytest.utils.AppConstants.THEME_LIGHT
import sagar.wellthytest.utils.LocaleHelper
import sagar.wellthytest.utils.PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE
import sagar.wellthytest.utils.PermissionUtils.getCurrentCity
import sagar.wellthytest.utils.PermissionUtils.isAccessFineLocationGranted
import sagar.wellthytest.utils.PermissionUtils.isLocationEnabled
import sagar.wellthytest.utils.PermissionUtils.requestAccessFineLocationPermission
import sagar.wellthytest.utils.PermissionUtils.showGPSNotEnabledDialog
import sagar.wellthytest.utils.SharedPreferencesHelper
import java.util.*


class WeatherReportActivity : AppCompatActivity() {

    private val TAG = "WeatherReportActivity"

    @SuppressLint("StaticFieldLeak")
    private val pref = SharedPreferencesHelper(WellthyApp.context)

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

    //to apply selected language when activity launch
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onLocaleCreate(newBase!!))
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

        //get last updated language if present else null
        var currentLanguage = pref.getString(SELECTED_LANGUAGE)
        if (currentLanguage == null){
            currentLanguage = LANGUAGE_ENGLISH
        }

        //set witch value by language
        swLanguage.isChecked = currentLanguage != LANGUAGE_ENGLISH

        swLanguage.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                currentLanguage = LANGUAGE_HINDI
                setLocale("hi")
            } else {
                currentLanguage = LANGUAGE_ENGLISH
                setLocale("en")
            }
            pref.putString(SELECTED_LANGUAGE, currentLanguage!!)
        }

    }

    //function to set selected language using LocaleHelper class
    private fun setLocale(languageCode: String) {
        val context: Context = LocaleHelper.setLocale(this, languageCode)
        val myLocale = Locale(languageCode)
        val res = context.resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)

        //restart activity to apply language change
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    //function to init and change theme
    private fun setupTheme(){

        //get last updated theme, if present else null
        var currentTheme = pref.getString(SELECTED_THEME)
        if (currentTheme == null){
            currentTheme = THEME_LIGHT
        }

        //set switch to true or false based on the theme value
        if (currentTheme == THEME_LIGHT){
            swTheme.isChecked = false
            changeTheme(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            swTheme.isChecked = true
            changeTheme(AppCompatDelegate.MODE_NIGHT_YES)
        }

        swTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                currentTheme = THEME_DARK
                changeTheme(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                currentTheme = THEME_LIGHT
                changeTheme(AppCompatDelegate.MODE_NIGHT_NO)
            }
            pref.putString(SELECTED_THEME, currentTheme!!)
        }

    }

    //function to change theme
    private fun changeTheme(mode: Int){
        AppCompatDelegate.setDefaultNightMode(mode)
    }

}