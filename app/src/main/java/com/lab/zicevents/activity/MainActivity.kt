package com.lab.zicevents.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.lab.zicevents.R
import com.lab.zicevents.data.database.user.UserDataSource
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.utils.OnActivityFabClickListener
import com.lab.zicevents.utils.OnRequestPermissionsListener
import com.lab.zicevents.utils.PermissionHelper
import com.lab.zicevents.utils.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var permissionsRequestCallback: OnRequestPermissionsListener
    private lateinit var activityFabCallback: OnActivityFabClickListener
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        viewModel.getAuthUserRealTimeUpdates()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.bottom_navigation_event,
                R.id.bottom_navigation_search,
                R.id.bottom_navigation_publication,
                R.id.bottom_navigation_chat,
                R.id.bottom_navigation_profile
            )
        )
        // Configure Bottom nav view
        navView.setupWithNavController(navController)

        // Configure navigation toolbar
        setSupportActionBar(main_toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Prevent recreation fragment when navigation item is reselect
        navView.setOnNavigationItemReselectedListener{
            // On Reselect do nothing....
        }

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            if (!main_toolbar.isVisible) main_toolbar.visibility = View.VISIBLE
            if (main_fab.isVisible) main_fab.hide()

            when (destination.id) {
                // R.id.bottom_navigation_profile -> main_toolbar.visibility = View.GONE
                R.id.bottom_navigation_publication -> main_fab.show()
                R.id.empty_publication_placeholder -> main_toolbar.navigationIcon = null
                else -> {
                }
            }
        }

        main_fab.setOnClickListener{
            activityFabCallback.onFabClick()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, SharedViewModelFactory())
            .get(SharedViewModel(UserRepository(UserDataSource()))::class.java)
        Log.d("Acitivty SHARED VM", viewModel.toString())
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Request permission callback
    override fun registerRequestPermissionsCallback(
        onRequestPermissionsListener: OnRequestPermissionsListener
    ) {
        permissionsRequestCallback = onRequestPermissionsListener
    }

    override fun registerFabClickCallback(activityFabClickListener: OnActivityFabClickListener) {
        activityFabCallback = activityFabClickListener
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionHelper.PERMS_RQ -> {
                // init result for callback
                val map = HashMap<String, Int>()
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()) {
                    // Add permission name + status
                    permissions.forEachIndexed { index, permission ->
                        map[permission] = grantResults[index]
                    }
                }
                var isAllGranted = true
                // Check if all permissions is granted
                map.forEach {
                    if (it.value == PackageManager.PERMISSION_DENIED) {
                        isAllGranted = false
                        Log.d(
                            this::class.java.simpleName,
                            "${it.key} permission was rejected by user"
                        )
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


