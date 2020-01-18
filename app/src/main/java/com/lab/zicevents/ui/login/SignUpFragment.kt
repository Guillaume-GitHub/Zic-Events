package com.lab.zicevents.ui.login


import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_sign_up.*

/**
 * Sign Up [Fragment] class.
 */
class SignUpFragment : Fragment(), View.OnClickListener{

    private lateinit var loginViewModel: LoginViewModel
    private var email: String? = null
    private var password: String? = null
    private var confirmPassword: String? = null
    private var progressDialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureViewModel()
        observeFormState()
        observeLoginUserSate()

        // Add click to submit button
        sign_up_fragment_login.setOnClickListener(this)

        //Trigger input email text changes
        sign_up_fragment_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                email = text?.toString()
                loginViewModel.signUpFormDataChanged(email = email, password = password, confirmPassword = confirmPassword)
            }
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        //Trigger input password text changes
        sign_up_fragment_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                password = text?.toString()
                loginViewModel.signUpFormDataChanged(email = email, password = password, confirmPassword = confirmPassword)
            }
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        //Trigger input password confirmation text changes
        sign_up_fragment_password_confirmation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                confirmPassword = text?.toString()
                loginViewModel.signUpFormDataChanged(email = email, password = password, confirmPassword = confirmPassword)
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
     * OnClick method Overriding
     */
    override fun onClick(view: View?) {
        view?.clearFocus()
        when (view) {
            sign_up_fragment_login -> createUserWithEmailAndPassword(sign_up_fragment_email.text.toString(), sign_up_fragment_password.text.toString())
            else -> {}
        }
    }

    /**
     * Observe form state and display errors messages if userProfileResult are not valid
     * Set submit button Enable / Disable
     */
    private fun observeFormState(){
        loginViewModel.loginFormState.observe(this, Observer {
            val loginState = it


            sign_up_fragment_login.isEnabled = loginState.isDataValid

            if (loginState.emailError != null) {
                sign_up_fragment_input_layout_email.error = getString(loginState.emailError)
                Log.d("Email error", getString(loginState.emailError))
            }
            else
                sign_up_fragment_input_layout_email.error = null

            if (loginState.passwordError != null) {
                sign_up_fragment_input_layout_password.error = getString(loginState.passwordError)
                Log.d("Password error", getString(loginState.passwordError))
            }
            else
                sign_up_fragment_input_layout_password.error = null

            if (loginState.confirmPassword != null) {
                sign_up_fragment_input_layout_password_confirmation.error = getString(R.string.invalid_password_confirmation)
                Log.d("Confirm error", getString(loginState.confirmPassword))
            }
            else
                sign_up_fragment_input_layout_password_confirmation.error = null
        })
    }

    /**
     * Observe user login result state
     * if user is null, display error
     * else show user profile
     */
    private fun observeLoginUserSate(){
        loginViewModel.loginUserState.observe(this, Observer {result ->
            if (result.user != null) {
                val email = result.user.email
                if (email != null)  navigateToProfileCreationFragment(email = email , phoneNumber = "0645789645")
                else Toast.makeText(context, R.string.profile_creation_unreadable_email, Toast.LENGTH_LONG).show()

            } else {
                val error = result.error
                if (error != null) Toast.makeText(context, getText(result.error), Toast.LENGTH_LONG).show()
            }
            showDialog(false) // Hide ProgressBAr
        })
    }

    /**
     * Try to create user with his e-mail and password and Observe result
     * @Success : Finish LoginActivity and Start MainActivity
     * @Fail : Display error
     */
    private fun createUserWithEmailAndPassword(email: String, password: String) {
        showDialog(true) // Show ProgressBAr
        loginViewModel.createUserWithEmailAndPassword(email, password)
    }

    /**
     * Display CreateProfileFragment with signed user information's
     * @param username username or pseudo of signed user, maybe null
     * @param email email address of signed user
     * @param phoneNumber phone number of user
     */
    private fun navigateToProfileCreationFragment(username: String? = null, email: String, phoneNumber: String? = null){
        val action = SignUpFragmentDirections.fromSignUpToCreateProfile(username, email, phoneNumber)
        findNavController().navigate(action)
    }


    /**
     * Display custom dialog with progress bar
     * @param display
     */
    //TODO : Improve with custom view
    private fun showDialog(display: Boolean){
        if (display){
            val dialog = Dialog(context!!)
            progressDialog = dialog
            val rootView = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null)
            val messageTextView: TextView = rootView.findViewById(R.id.custom_progress_dialog_message)

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // hide window
            messageTextView.text = getString(R.string.create_profile_dialog_message) // set message
            dialog.setContentView(rootView) // add custom view
            dialog.setCancelable(false)
            dialog.show()

        } else {
            this.progressDialog?.dismiss()
        }
    }
}
