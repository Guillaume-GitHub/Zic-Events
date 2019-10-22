package com.lab.zicevents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lab.zicevents.ui.login.LoginViewModel
import com.lab.zicevents.ui.login.LoginViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var navController : NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        //check if user is sign in
        if (isUserAuth()) launchMainActivity()
        else {
            navController = findNavController(R.id.login_nav_host_fragment)
        }
    }

    /**
     * Check if an user are already authenticated
     *@return True = User already authenticated, False = No user authenticate
     */
    private fun isUserAuth(): Boolean{
        return auth.currentUser != null
    }

    /**
     * Start MainActivity and finish LoginActivity
     */
    private fun launchMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }
}
