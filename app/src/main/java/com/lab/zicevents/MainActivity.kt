package com.lab.zicevents

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.lab.zicevents.utils.OnRequestPermissionsListener
import com.lab.zicevents.utils.PermissionHelper
import com.lab.zicevents.utils.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: BaseActivity() {

    private lateinit var permissionsRequestCallback: OnRequestPermissionsListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.bottom_navigation_event,
                R.id.bottom_navigation_search,
                R.id.bottom_navigation_publication ,
                R.id.bottom_navigation_chat,
                R.id.bottom_navigation_profile
            )
        )

        main_toolbar.setupWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            when(destination.id) {
                R.id.bottom_navigation_profile ->
                    if (main_toolbar.isVisible) main_toolbar.visibility = View.GONE
                else ->  if (!main_toolbar.isVisible) main_toolbar.visibility = View.VISIBLE
            }
        }
    }

    override fun registerRequestPermissionsCallback(
        onRequestPermissionsListener: OnRequestPermissionsListener) {
       permissionsRequestCallback = onRequestPermissionsListener
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionHelper.PERMS_RQ -> {
                // init result for callback
                val map = HashMap<String,Int>()
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()) {
                    // Add permission name + status
                    permissions.forEachIndexed { index, permission  ->
                        map[permission] = grantResults[index]
                    }
                }
                var isAllGranted = true
                // Check if all permissions is granted
                map.forEach {
                    if (it.value == PackageManager.PERMISSION_DENIED){
                        isAllGranted = false
                        Log.d(this::class.java.simpleName, "${it.key} permission was rejected by user")
                    }
                }
                // Pass result in callback
                permissionsRequestCallback.onRequestPermissions(isAllGranted, map)
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}
