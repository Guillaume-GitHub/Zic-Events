package com.lab.zicevents.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.lab.zicevents.MainActivity

import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_login_main.*

class LoginMainFragment : Fragment(), View.OnClickListener {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureViewModel()
        observeFormState()
        // Add click listener to submit button
        login.setOnClickListener(this)
        // Add click listener to Sign Up text view
        sign_up.setOnClickListener(this)
        //Trigger input username text changes
        username.addTextChangedListener(object : TextWatcher {
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

    /**
     * Observe form state and display errors messages if data are not valid
     * Set submit button Enable / Disable
     */
    private fun observeFormState(){
        loginViewModel.loginFormState.observe(this, Observer {
            val loginState = it

            login.isEnabled = loginState.isDataValid

            if (loginState.emailError != null) inputLayoutUsername.error = getString(loginState.emailError)
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
            sign_up -> findNavController().navigate(R.id.action_navigation_login_main_to_navigation_login_sign_up)
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
                if (it) {
                    startActivity(Intent(context, MainActivity::class.java))
                    activity?.finish()
                    // Destroy LoginActivity TODO : Intent to MainActivity
                }
                else{
                    //TODO : Fail case
                }

                showProgressBar(false) // Hide progressBar
                Toast.makeText(context, R.string.signIn_fail, Toast.LENGTH_LONG).show()
            })
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
