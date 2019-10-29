package com.lab.zicevents.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lab.zicevents.MainActivity

import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment(), View.OnClickListener {

    private lateinit var callbackManager: CallbackManager
    private lateinit var loginViewModel: LoginViewModel
    // Configure Google Sign In
    private lateinit var gso : GoogleSignInOptions
    private val RC_GOOGLE_SIGN_IN = 10

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureViewModel()
        observeFormState()

        // Configure Google sign in Options
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        // Add click listener to buttons
        login.setOnClickListener(this)
        google_login.setOnClickListener(this)
        facebook_login.setOnClickListener(this)

        //Trigger input username text changes
        username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                loginViewModel.signInFormDataChanged(email = text.toString(), password = "")
            }
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //Trigger input password text changes
        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                loginViewModel.signInFormDataChanged(email = "", password = password.text.toString())
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
            google_login -> startActivityForResult(GoogleSignIn.getClient(context!!, gso).signInIntent, RC_GOOGLE_SIGN_IN)
            facebook_login -> startFacebookAuthentication()
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
                }
                else{
                    Toast.makeText(context, R.string.signIn_fail, Toast.LENGTH_LONG).show()
                }

                showProgressBar(false) // Hide progressBar

            })
    }

    /**
     * Try to sign in user with his google account and Observe result
     * @Success : Finish LoginActivity and Start MainActivity
     * @Fail : Display error
     */
    private fun firebaseSignInWithGoogle(account: GoogleSignInAccount){
        showProgressBar(true) // Show ProgressBAr

        loginViewModel.signInWithGoogle(account)
            .observe(this, Observer {
                if (it) {
                    startActivity(Intent(context, MainActivity::class.java))
                    activity?.finish()
                }
                else {
                    Toast.makeText(context, R.string.signIn_fail, Toast.LENGTH_LONG).show()
                }

                showProgressBar(false) // Hide progressBar
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                Log.d("TAG", "Google sign in")
                firebaseSignInWithGoogle(account!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e)
                // ...
            }
        }

        else {
            // Result Callback Facebook SignIn
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     *  Register Facebook login callback and observe result
     */
    private fun startFacebookAuthentication() {
        callbackManager = CallbackManager.Factory.create()

        val lm = LoginManager.getInstance()

        //Observe result
        lm.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result?.accessToken != null) firebaseSignInWithFacebook(result.accessToken)
            }

            override fun onCancel() {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(error: FacebookException?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        // Start Facebook connection view
        lm.logIn(this, listOf("email", "public_profile"))
    }


    /**
     * Try to sign in user with his Facebook token and Observe result
     * @Success : Finish LoginActivity and Start MainActivity
     * @Fail : Display error
    */
    fun firebaseSignInWithFacebook(token: AccessToken){
        showProgressBar(true) // Show ProgressBAr

        loginViewModel.signInWithFacebook(token)
            .observe(this, Observer {
                if (it) {
                    startActivity(Intent(context, MainActivity::class.java))
                    activity?.finish()
                }
                else {
                    Toast.makeText(context, R.string.signIn_fail, Toast.LENGTH_LONG).show()
                }

                showProgressBar(false) // Hide progressBar
            })
    }

}
