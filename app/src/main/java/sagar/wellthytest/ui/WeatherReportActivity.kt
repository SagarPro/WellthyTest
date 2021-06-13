package sagar.wellthytest.ui

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_weather_report.*
import kotlinx.android.synthetic.main.item_language.*
import kotlinx.android.synthetic.main.item_theme.*
import sagar.wellthytest.R
import sagar.wellthytest.api.ApiResponse
import sagar.wellthytest.base.BaseActivity
import sagar.wellthytest.interfaces.AlertDialogListener
import sagar.wellthytest.model.LatLng
import sagar.wellthytest.model.WeatherReportModel
import sagar.wellthytest.utils.AppConstants.LANGUAGE_ENGLISH
import sagar.wellthytest.utils.AppConstants.LANGUAGE_HINDI
import sagar.wellthytest.utils.AppConstants.SELECTED_LANGUAGE
import sagar.wellthytest.utils.AppConstants.SELECTED_THEME
import sagar.wellthytest.utils.AppConstants.THEME_DARK
import sagar.wellthytest.utils.AppConstants.THEME_LIGHT
import sagar.wellthytest.utils.ConnectivityReceiver
import sagar.wellthytest.utils.LocaleHelper
import sagar.wellthytest.utils.PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE
import sagar.wellthytest.utils.PermissionUtils.getCurrentCity
import sagar.wellthytest.utils.PermissionUtils.isAccessFineLocationGranted
import sagar.wellthytest.utils.PermissionUtils.isLocationEnabled
import sagar.wellthytest.utils.PermissionUtils.requestAccessFineLocationPermission
import sagar.wellthytest.utils.PermissionUtils.showGPSNotEnabledDialog
import sagar.wellthytest.utils.SharedPreferencesHelper
import sagar.wellthytest.utils.ViewUtils.hide
import sagar.wellthytest.utils.ViewUtils.show
import sagar.wellthytest.utils.ViewUtils.showCustomMessage
import sagar.wellthytest.viewmodel.WeatherReportViewModel
import java.lang.IllegalArgumentException
import javax.inject.Inject

class WeatherReportActivity: BaseActivity<WeatherReportViewModel>(), ConnectivityReceiver.ConnectivityReceiverListener, AlertDialogListener {

    private val TAG = "WeatherReportActivity"

    @Inject
    lateinit var pref: SharedPreferencesHelper

    private var currentLatLng: LatLng? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    lateinit var connectivityReceiver: ConnectivityReceiver

    override fun provideLayout(): Int {
        return R.layout.activity_weather_report
    }

    override fun provideViewModelClass(): Class<WeatherReportViewModel> {
        return WeatherReportViewModel::class.java
    }

    override fun initViews() {
        //we can initialize any views here
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBar.hide()
        noInternetView.visibility = View.GONE
        locationPermissionDeniedView.visibility = View.GONE
        setupLanguage()
        setupTheme()
        requestLocationPermissionOnUserDemand()
    }

    //to apply selected language when activity launch
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onLocaleCreate(newBase!!))
    }

    override fun onStart() {
        super.onStart()

        //init network connection listener
        try {
            ConnectivityReceiver.connectivityReceiverListener = this
            connectivityReceiver = ConnectivityReceiver()

            //to check if network changed
            val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
            try{
                unregisterReceiver(connectivityReceiver) //unregister receiver
            } catch (e: IllegalArgumentException){}
            registerReceiver(connectivityReceiver, intentFilter) //register receiver again with new network connection
        } catch (e: Exception){}
    }

    override fun onDestroy() {
        super.onDestroy()
        try{
            unregisterReceiver(connectivityReceiver)
        } catch (e: IllegalArgumentException){}
    }

    private fun fetchLocation(){
        //if currentLatLng is null check for location permission and get latLng
        //if currentLatLng is not null then no need to do anything
        if (currentLatLng == null) {
            when {
                this.isAccessFineLocationGranted() -> {
                    locationPermissionDeniedView.visibility = View.GONE
                    when {
                        this.isLocationEnabled() -> {
                            setUpLocationListener()
                        }
                        else -> {
                            this.showGPSNotEnabledDialog()
                        }
                    }
                } else -> {
                    this.requestAccessFineLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
                }
            }
        } else {
            locationPermissionDeniedView.visibility = View.GONE
        }
    }

    //to request for location permission after denied by user
    private fun requestLocationPermissionOnUserDemand(){
        locationPermissionDeniedView.setOnClickListener {
            this.requestAccessFineLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    //function to get last known location if present else get new location
    private fun setUpLocationListener() {
        progressBar.show()
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
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

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
        getCityAndCallApi()
    }

    private fun getCityAndCallApi(){
        progressBar.hide()
        val city = baseContext.getCurrentCity(currentLatLng!!)
        Log.d(TAG, "City: $city")

        //check if we are getting city or null
        try {
            tvCityName.text = city
            getWeatherReport(city!!)
        } catch (e: java.lang.Exception){
            //if we are not getting city name for any reason, ask user to get city name again
            this.showCustomMessage(
                    resources.getString(R.string.alert_dialog_title_city_name),
                    resources.getString(R.string.alert_dialog_message_city_name),
                    this)
        }
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
                    fetchLocation()
                } else {
                    //display location permission denied message
                    locationPermissionDeniedView.visibility = View.VISIBLE
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
                setLocale("hi") //to set hindi language
            } else {
                currentLanguage = LANGUAGE_ENGLISH
                setLocale("en") //to set english language
            }
            pref.putString(SELECTED_LANGUAGE, currentLanguage!!)
        }

    }

    //function to set selected language using LocaleHelper class
    private fun setLocale(languageCode: String) {
        LocaleHelper.setLocale(this, languageCode)
        recreate() // recreate view to apply language changes
    }

    //function to init and change theme
    private fun setupTheme(){

        //get last updated theme, if present else null
        var currentTheme = pref.getString(SELECTED_THEME)
        if (currentTheme == null){
            currentTheme = THEME_LIGHT
        }

        //set switch to true or false based on the theme value
        swTheme.isChecked = currentTheme != THEME_LIGHT

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

    //call api after getting city name
    private fun getWeatherReport(city: String){
        //observer to observe for api response
        getViewModel().getWeatherReport(city).observe(this, androidx.lifecycle.Observer { weatherReport ->
            if (weatherReport != null) {
                when (weatherReport.status) {
                    ApiResponse.Status.LOADING -> {
                        progressBar.show()
                    }
                    ApiResponse.Status.SUCCESS -> {
                        progressBar.hide()
                        if (weatherReport.response != null) {
                            updateWeatherReport(weatherReport.response)
                        }
                    }
                    ApiResponse.Status.ERROR -> {
                        progressBar.hide()
                    }
                }
            }
        })
    }

    //update weather info, once we receive from api
    private fun updateWeatherReport(weatherReport: WeatherReportModel){
        //set temperature
        tvTemperature.text = weatherReport.temperature?.toString()
        celsius.text = "\u2103"

        //load weather description array and display
        val weatherDesc = weatherReport.weather_descriptions
        if (weatherDesc != null && weatherDesc.isNotEmpty()){
            var displayWeatherDesc = ""
            for (i in 0 until weatherDesc.size-1){
                displayWeatherDesc = "${weatherDesc[i]}/n"
            }

            //to append with last element without /n
            displayWeatherDesc = "$displayWeatherDesc${weatherDesc[weatherDesc.size-1]}"

            tvWeatherDescription.text = displayWeatherDesc
        }

        //load weather images
        val weatherImages = weatherReport.weather_icons
        if (weatherImages != null && weatherImages.isNotEmpty()){
            //glide library to load an image
            Glide.with(this).load(weatherImages[0]).into(ivWeatherImage)
        }
    }

    //network connection listener
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected){
            noInternetView.visibility = View.GONE
            fetchLocation()
        } else {
            noInternetView.visibility = View.VISIBLE
        }
    }

    //function to get city name, if user demand to get city name again
    override fun userRequestedToGetCityName() {
        getCityAndCallApi()
    }

}