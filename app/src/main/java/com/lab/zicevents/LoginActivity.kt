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

    private lateinit var auth: FirebaseAuth
    private lateinit var navController : NavController
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        //check if user is sign in
        if (isUserAuth()){
            configureViewModel()
            loginViewModel.getFirestoreUser(auth.currentUser!!.uid)
            observeFirestoreUserProfile()
        } else {
            configureNavController()
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

    /**
     *  Instantiate a LoginViewModel class from LoginViewModelFactory
     */
    private fun configureViewModel(){
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
    }

    /**
     * Configure nav controller and appBarConfiguration
     */
    private fun configureNavController(){
        navController = findNavController(R.id.login_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        login_toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    /**
     * Observe result of firestore user data fetching operation
     * Show different views depending to result
     */
    private fun observeFirestoreUserProfile(){
        loginViewModel.profileUserState.observe(this, Observer {userProfile ->
            if (userProfile.firestoreUser != null) launchMainActivity()
            else  Toast.makeText(this, "User signed in with no profile into database", Toast.LENGTH_LONG).show()
            // TODO : REDIRECT TO CREATION PROFILE VIEW OR LOGOUT CURRENT USER
        })
    }
}
