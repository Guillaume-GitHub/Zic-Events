package com.lab.zicevents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.lab.zicevents.ui.login.LoginViewModel
import com.lab.zicevents.ui.login.LoginViewModelFactory
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
