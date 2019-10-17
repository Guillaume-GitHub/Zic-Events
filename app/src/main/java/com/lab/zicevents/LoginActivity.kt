package com.lab.zicevents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lab.zicevents.ui.login.LoginViewModel
import com.lab.zicevents.ui.login.LoginViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        //check if user is sign in
        val currentUser = auth.currentUser
        this.launchMainActivity(currentUser)

        login.setOnClickListener(this)
    }

    /**
     * OnClick method Overriding
     */
    override fun onClick(view: View?) {
        when (view) {
            login -> signInWithEmailAndPassword()
            else -> {}
        }
    }

    /**
     * Try to sign in user with his e-mail and password and Observe result
     * @Success : Finish LoginActivity and Start MainActivity
     * @Fail : Display error
     */
    private fun signInWithEmailAndPassword() {
        showProgressBar(true) // Show ProgressBAr

        loginViewModel.login(username.text.toString(), password.text.toString())
            .observe(this, Observer {
                if (it) finish() // Destroy LoginActivity TODO : Intent to MainActivity

                //TODO : Fail case
                showProgressBar(false) // Hide progressBar
            })
    }


    /**
     * Check current user object passed in param and start MainActivity.kt if non null
     * @param currentUser is FirebaseUser object, must be null
     */
    private fun launchMainActivity(currentUser: FirebaseUser?){
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
        else {
            configureViewModel()
        }
    }

    /**
     *  Instantiate a LoginViewModel class from LoginViewModelFactory
     */
    private fun configureViewModel(){
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
    }

    /**
     * Show / Hide ProgressBar
     * @param display true = Show, False = Hide
     */
    private fun showProgressBar(display: Boolean){
        if (display){
            loading.visibility = View.VISIBLE
            loading.isIndeterminate = true
        }
        else{
            loading.visibility = View.GONE
            loading.isIndeterminate = false
        }
    }
}
