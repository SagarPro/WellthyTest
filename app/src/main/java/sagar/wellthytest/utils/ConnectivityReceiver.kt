package sagar.wellthytest.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class ConnectivityReceiver: BroadcastReceiver() {

    companion object {
        lateinit var connectivityReceiverListener: ConnectivityReceiverListener
    }

    @SuppressLint("NewApi")
    override fun onReceive(context: Context?, intent: Intent?) {
        val isConnected = (context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }
        try {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected)
        } catch (e: Exception){}
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

}