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
        if (isUserAuth()) launchMainActivity()
        else {
            configureViewModel()
            observeFormState()
            // Add click listener to submit button
            login.setOnClickListener(this)
            //Trigger input username text changes
            username.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(text: Editable?) {
                    loginViewModel.loginDataChanged(email = text.toString(), password = "")
                }
                override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            //Trigger input password text changes
            password.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(text: Editable?) {
                    loginViewModel.loginDataChanged("", password = text.toString())
                }
                override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
            })
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
     * Observe form state and display errors messages if data are not valid
     * Set submit button Enable / Disable
     */
    private fun observeFormState(){
        loginViewModel.loginFormState.observe(this, Observer {
            val loginState = it

            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) inputLayoutUsername.error = getString(loginState.usernameError)
            else inputLayoutUsername.error = null

            if (loginState.passwordError != null) inputLayoutPassword.error = getString(loginState.passwordError)
            else inputLayoutPassword.error = null
        })
    }

    /**
     * OnClick method Overriding
     */
    override fun onClick(view: View?) {
        view?.clearFocus()
        when (view) {
            login -> signInWithEmailAndPassword(username.text.toString(), password.text.toString())
            else -> {}
        }
    }

    /**
     * Try to sign in user with his e-mail and password and Observe result
     * @Success : Finish LoginActivity and Start MainActivity
     * @Fail : Display error
     */
    private fun signInWithEmailAndPassword(email: String, password: String) {
        showProgressBar(true) // Show ProgressBAr

        loginViewModel.signInWithEmailAndPassword(email, password)
            .observe(this, Observer {
                if (it) finish() // Destroy LoginActivity TODO : Intent to MainActivity

                //TODO : Fail case
                showProgressBar(false) // Hide progressBar
                Toast.makeText(this, R.string.signIn_fail,Toast.LENGTH_LONG).show()
            })
    }

    /**
     * Try to create user with his e-mail and password and Observe result
     * @Success : Finish LoginActivity and Start MainActivity
     * @Fail : Display error
     */
    private fun createUserWithEmailAndPassword(email: String, password: String) {
        showProgressBar(true) // Show ProgressBAr

        loginViewModel.createUserWithEmailAndPassword(email, password)
            .observe(this, Observer {
                if (it) finish() // Destroy LoginActivity TODO : Intent to MainActivity

                //TODO : Fail case
                showProgressBar(false) // Hide progressBar
                Toast.makeText(this, R.string.user_creation_fail,Toast.LENGTH_LONG).show()
            })
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
     * Show / Hide ProgressBar
     * @param display True = Show, False = Hide
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
