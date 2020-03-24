package com.lab.zicevents.utils.adapter

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
    }
}