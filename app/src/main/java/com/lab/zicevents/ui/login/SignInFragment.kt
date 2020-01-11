package com.lab.zicevents.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*

import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.lab.zicevents.MainActivity

import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User
import kotlinx.android.synthetic.main.fragment_sign_in.*

/**
 * Sign In [Fragment] class.
 */

class SignInFragment : Fragment(), View.OnClickListener {

    private lateinit var callbackManager: CallbackManager
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var  currentUser: FirebaseUser
    private val RC_GOOGLE_SIGN_IN = 10

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureViewModel()
        observeFormState()
        observeSignInResult()
        observeFirestoreUserProfile()


        // Add click listener to buttons
        login.setOnClickListener(this)
        google_login.setOnClickListener(this)
        facebook_login.setOnClickListener(this)
        forgot_password.setOnClickListener(this)

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
     *  Instantiate a LoginViewModel class from LoginViewModelFactory
     */
    private fun configureViewModel(){
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
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
     * Observe Sign in result state
     * if user is null, display error
     * else show user profile
     */
    private fun observeSignInResult(){
        loginViewModel.loginUserState.observe(this, Observer {result ->
            val firebaseUser = result.user
            if (firebaseUser != null) { // Check if username and email are not null
                currentUser = firebaseUser
                loginViewModel.getFirestoreUser(firebaseUser.uid)
            } else {
                showProgressBar(false) // Hide ProgressBAr
                val error = result.error
                if (error != null) Toast.makeText(context, getText(result.error), Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * Observe firestore user profile to check if current auth user is save in firestore
     */
    private fun observeFirestoreUserProfile(){
        loginViewModel.dataResult.observe(this, Observer {
            showProgressBar(false)
            val userProfile = it

            if (userProfile.data is User?){
                when {
                    // Case User is already create in firestore -> Start main activity
                    userProfile.data != null -> startMainActivity()
                    // Case : User is present in auth but not in Firestore -> Navigate to profile creation fragment
                    currentUser.email != null -> navigateToProfileCreationFragment(currentUser.displayName,currentUser.email!!,currentUser.phoneNumber)
                    // Case profile fetching failed -> Display error message
                    userProfile.error != null -> Toast.makeText(context, getText(userProfile.error), Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(context, getText(R.string.incomplete_sign_in_account), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    /**
     * Display CreateProfileFragment with signed user information's
     * @param pseudo username or pseudo of signed user
     * @param email email address of signed user
     * @param phoneNumber phone number of user
     */
    private fun navigateToProfileCreationFragment(displayName: String?, email: String, phoneNumber: String?){
        val action = SignInFragmentDirections.actionFromSignInToCreateProfile(displayName,email,phoneNumber)
        findNavController().navigate(action)
    }

    /**
     * OnClick method Overriding
     */
    override fun onClick(view: View?) {
        view?.clearFocus()
        when (view) {
            login -> emailAndPasswordAuth(username.text.toString(), password.text.toString())
            google_login -> googleAuth()
            facebook_login -> facebookAuth()
            forgot_password -> navigateToResetPassword(null)
            else -> {}
        }
    }

    /**
     * Get credential from email/password and pass it to signInWithCredential method
     */
    private fun emailAndPasswordAuth(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email,password)
        signInWithCredential(credential)
    }

    /**
     * Get credential from google account and pass it to signInWithCredential method
     * Configure google sign in option and start google sign in intent
     * Retrieve result of google account in onActivityResult
     */
    private fun googleAuth(){
        // Configure Google sign in Options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Start google sign in intent
        startActivityForResult(GoogleSignIn.getClient(context!!, gso).signInIntent, RC_GOOGLE_SIGN_IN)
    }

    /**
     *  Register Facebook login callback
     *  Start Facebook sign in view and observe result get in onActivityResult
     *  OnSuccess : get facebook token and credential and pass it to signInWithCredential methods
     *  onCancel : display error message
     *  onError : display error message
     */
    private fun facebookAuth() {
        callbackManager = CallbackManager.Factory.create()

        // Start Facebook connection view
        val lm = LoginManager.getInstance()
        lm.logIn(this, listOf("email", "public_profile"))

        //Observe result
        lm.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result?.accessToken != null) {
                    val token = result.accessToken.token // get token
                    val credential = FacebookAuthProvider.getCredential(token) // get authCredential
                    signInWithCredential(credential) // Sign In
                }
            }

            override fun onCancel() {}

            override fun onError(error: FacebookException?) {
                Log.w("Facebook Exception", error)
            }
        })
    }

    /**
     * Launch Kotlin Coroutine to sign in user with credential
     * Observe result with loginViewModel.loginUserState
     */
    fun signInWithCredential(credential: AuthCredential){
        showProgressBar(true) // Show ProgressBAr
        loginViewModel.signInWithCredential(credential)
    }

    /**
     * Display ResetPasswordFragment
     * @param email email address of signed user (May be null)
     */
    private fun navigateToResetPassword(email: String?){
        val action = SignInFragmentDirections.actionFromSignInToResetPassword(email)
        findNavController().navigate(action)
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

    /**
     * Start main activity and finish LoginActivity
     */
    private fun startMainActivity(){
        startActivity(Intent(context, MainActivity::class.java))
        activity?.finish() // finish this
    }

    // Catch activity results
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                Log.d("TAG", "Google sign in")
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                signInWithCredential(credential)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e)
                // ...
            }

        } else {
            // Result Callback Facebook SignIn
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}
