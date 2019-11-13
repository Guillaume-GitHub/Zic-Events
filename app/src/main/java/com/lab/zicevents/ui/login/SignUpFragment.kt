package com.lab.zicevents.ui.login


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
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
                loginViewModel.signUpFormDataChanged(email = text.toString(), password = "")
            }
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        //Trigger input password text changes
        sign_up_fragment_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                loginViewModel.signUpFormDataChanged("", password = text.toString())
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
     * Observe form state and display errors messages if data are not valid
     * Set submit button Enable / Disable
     */
    private fun observeFormState(){
        loginViewModel.loginFormState.observe(this, Observer {
            val loginState = it

            sign_up_fragment_login.isEnabled = loginState.isDataValid

            if (loginState.emailError != null) sign_up_fragment_input_layout_email.error = getString(loginState.emailError)
            else sign_up_fragment_input_layout_email.error = null

            if (loginState.passwordError != null) sign_up_fragment_input_layout_password.error = getString(loginState.passwordError)
            else sign_up_fragment_input_layout_password.error = null

            if (sign_up_fragment_username.text.isNullOrBlank()) sign_up_fragment_input_layout_username.error = getString(R.string.invalid_username)
            else sign_up_fragment_input_layout_username.error = null
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
                showProgressBar(false) // Hide ProgressBAr

                navigateToProfileCreationFragment(sign_up_fragment_username.text.toString(), result.user.email!!)
                Toast.makeText(context, "User ID = ${result.user.uid}", Toast.LENGTH_LONG).show()
            } else {
                showProgressBar(false) // Hide ProgressBAr
                val error = result.error
                if (error != null) Toast.makeText(context, getText(result.error), Toast.LENGTH_LONG).show()
            }
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
    }

    /**
     * Display CreateProfileFragment with signed user information's
     * @param pseudo username or pseudo of signed user
     * @param email email address of signed user
     */
    private fun navigateToProfileCreationFragment(pseudo: String, email: String){
        val action = SignUpFragmentDirections.fromSignUpToCreateProfile(pseudo,email,null)
        findNavController().navigate(action)
    }

    /**
     * Show / Hide ProgressBar
     * @param display True = Show, False = Hide
     */
    private fun showProgressBar(display: Boolean){
        if (display){
            sign_up_fragment_loading.visibility = View.VISIBLE
            sign_up_fragment_loading.isIndeterminate = true
        }
        else{
            sign_up_fragment_loading.visibility = View.GONE
            sign_up_fragment_loading.isIndeterminate = false
        }
    }
}
