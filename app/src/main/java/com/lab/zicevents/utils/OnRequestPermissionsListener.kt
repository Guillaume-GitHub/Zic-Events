package com.lab.zicevents.utils

/**
 * Request Permission Handler
 */
interface OnRequestPermissionsListener {
    fun onRequestPermissions(isGranted: Boolean, grantResult: Map<String,Int>)
}