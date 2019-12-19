package com.lab.zicevents

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.lab.zicevents.ui.login.LoginViewModel
import com.lab.zicevents.ui.login.LoginViewModelFactory

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        this.auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        Log.d("SPLASH CREENN","ACTIVITY")

        //check if user is sign in
        if (isUserAuth()){
            configureViewModel()
            loginViewModel.getFirestoreUser(auth.currentUser!!.uid)
            observeFirestoreUserProfile()
        } else {
            startLoginActivity()
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
     * Start LoginActivity and finish LoginActivity
     */
    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        this.finish()
    }

    /**
     * Start LoginActivity and finish LoginActivity
     */
    private fun startMainActivity() {
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
     * Observe result of firestore user data fetching operation
     * Show different views depending to result
     */
    private fun observeFirestoreUserProfile(){
        loginViewModel.profileUserData.observe(this, Observer { userProfile ->
            if (userProfile.firestoreUser != null) startMainActivity()
            else  {
                auth.currentUser!!.delete() // Delete user auth with no profile
                Toast.makeText(this, "User signed in with no profile into database", Toast.LENGTH_LONG).show()
                startLoginActivity()
            }
        })
    }

}
