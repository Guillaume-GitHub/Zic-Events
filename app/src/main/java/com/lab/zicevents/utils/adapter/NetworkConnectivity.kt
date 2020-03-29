package com.lab.zicevents.utils.adapter

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import java.net.InetAddress

class NetworkConnectivity {
    companion object {
        fun isConnected(host: String = "google.com"): Boolean {
            return try {
                Log.d(this::class.java.simpleName, " Try to resolve host $host")
                val addr = InetAddress.getByName(host)
                Log.d(this::class.java.simpleName, " Resolution of $host host => $addr")
                addr != null
            } catch (e: Exception) {
                Log.e(NetworkConnectivity::class.java.simpleName, " error when call isInternetAvailable()", e.cause)
                false
            }
        }

        fun isOnline(context: Context?): Boolean {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}