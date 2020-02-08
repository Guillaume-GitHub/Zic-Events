package com.lab.zicevents.utils.base

import androidx.appcompat.app.AppCompatActivity
import com.lab.zicevents.utils.OnActivityFabClickListener
import com.lab.zicevents.utils.OnRequestPermissionsListener

/**
 * Base Activity
 */
abstract class BaseActivity: AppCompatActivity() {
    open fun registerRequestPermissionsCallback(onRequestPermissionsListener: OnRequestPermissionsListener){}
    open fun registerFabClickCallback(activityFabClickListener: OnActivityFabClickListener){}
}
