package com.lab.zicevents.utils
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lab.zicevents.MainActivity
import java.util.jar.Manifest

class PermissionHelper {

    companion object {
        const val PERMS_RQ = 3001
        // All permission for storage access
        val STORAGE_PERMISSIONS =
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val LOCATION_PERMISSIONS =
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Check if permissions was granted
     * @param context context
     * @param permissions array of permission to check
     * @return ArrayList of denied permission or null if all granted
     */
    fun checkPermissions(context: Context, permissions: Array<String>): ArrayList<String>? {
        val permResult = HashMap<String,Int>()
        val permsDenied = ArrayList<String>()
        // Check permission state
        for (permission in permissions) {
            permResult[permission] = ContextCompat.checkSelfPermission(context, permission)
        }
        // Get denied permissions
        permResult.forEach {
            if (it.value != PackageManager.PERMISSION_GRANTED) {
                Log.w(this::class.java.simpleName, "'${it.key}' Permission is DENIED" )
                permsDenied.add(it.key)
            }
        }
        // Return all Denied permissions in array or null if all granted
        return if (permsDenied.isEmpty()){
            null
        }
        else {
            val perms = ArrayList<String>()
            perms.addAll(permsDenied)
            perms
        }
    }

    /**
     * Register callback to handle request permission result
     * and prompt User to accept permissions
     * @param activity current activity
     * @param permissions array of permission to ask
     * @param callback callback who handle request permission result
     */
    fun askRequestPermissions(activity: Activity?, permissions: Array<String>,
                              callback: OnRequestPermissionsListener) {
        if (activity != null) {
            (activity as MainActivity).registerRequestPermissionsCallback(callback)
            ActivityCompat.requestPermissions(activity, permissions, PERMS_RQ)
        }
    }
}