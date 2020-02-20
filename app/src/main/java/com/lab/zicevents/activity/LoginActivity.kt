package com.lab.zicevents.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.lab.zicevents.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onStart() {
        super.onStart()
        configureNavController()
    }

    /**
     * Configure nav controller and appBarConfiguration
     */
    private fun configureNavController(){
        navController = findNavController(R.id.login_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        login_toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}
