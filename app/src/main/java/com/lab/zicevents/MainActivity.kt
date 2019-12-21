package com.lab.zicevents

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.bottom_navigation_deal, R.id.bottom_navigation_event, R.id.bottom_navigation_deal_management , R.id.bottom_navigation_chat, R.id.bottom_navigation_profile
            )
        )

        main_toolbar.setupWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{ controller, destination, arguments ->
            when(destination.id) {
                R.id.bottom_navigation_profile ->
                    if (main_toolbar.isVisible) main_toolbar.visibility = View.GONE
                else ->  if (!main_toolbar.isVisible) main_toolbar.visibility = View.VISIBLE
            }
        }
    }
}
