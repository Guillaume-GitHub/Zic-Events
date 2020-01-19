package com.lab.zicevents.utils

/**
 * Request Permission Handler
 */
interface OnRequestPermissionsListener {
    fun onRequestPermissions(permissionsResult: Map<String,Int>)
}