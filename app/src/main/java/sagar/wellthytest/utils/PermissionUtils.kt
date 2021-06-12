package sagar.wellthytest.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import sagar.wellthytest.R
import java.util.*

object PermissionUtils {

    const val LOCATION_PERMISSION_REQUEST_CODE = 201

    //function to request permission from user
    fun AppCompatActivity.requestAccessFineLocationPermission(requestId: Int) {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                requestId
        )
    }

    //function to check if location permissions are granted or not
    fun Context.isAccessFineLocationGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    //function to check if location of the device is enabled or not
    fun Context.isLocationEnabled(): Boolean {
        val locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //function to show enable gps dialog
    fun Context.showGPSNotEnabledDialog() {
        AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.enable_gps))
                .setMessage(this.getString(R.string.required_for_this_app))
                .setCancelable(false)
                .setPositiveButton(this.getString(R.string.enable)) { _, _ ->
                    this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .show()
    }

    //function to get city by latlang
    fun Context.getCurrentCity(currentLatLng: LatLng): String?{
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1)
            if (addresses != null && addresses.size > 0) {
                return addresses[0].locality
            }
        } catch (e: Exception){}
        return null
    }

}