package com.lab.zicevents.utils.base

import android.util.Log
import java.net.InetAddress

class NetworkConnectivityHelper {
    companion object {
        fun isInternetAvailable(host: String = "google.com"): Boolean {
            return try {
                Log.d(this::class.java.simpleName, " Try to resolve host $host")
                val addr = InetAddress.getByName(host)
                Log.d(this::class.java.simpleName, " Resolution of $host host => $addr")
                addr != null
            } catch (e: Exception) {
                Log.e(NetworkConnectivityHelper::class.java.simpleName, " error when call isInternetAvailable()", e.cause)
                false
            }
        }
    }
}