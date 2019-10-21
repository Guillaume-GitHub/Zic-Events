package com.lab.zicevents.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lab.zicevents.R
import com.lab.zicevents.data.login.LoginRepository

class LoginViewModel(private val loginRepository: LoginRepository): ViewModel() {

    private val loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = loginForm

    /**
     * Send email and password to the repository to signIn user with email and password (async task)
     * @return LiveData< Boolean> Observable boolean
     */
    fun signInWithEmailAndPassword(email: String, password: String): LiveData< Boolean> {
        return  loginRepository.signInWithEmailAndPassword(email, password)
    }

    /**
     * Send email and password to the repository to create new user with email and password (async task)
     * @return LiveData< Boolean> Observable boolean
     */
    fun createUserWithEmailAndPassword(email: String, password: String): LiveData< Boolean> {
        return  loginRepository.createUserWithEmailAndPassword(email, password)
    }

    /**
     * Change loginForm LiveData value
     * @param email email input text
     * @param password password input text
     */
    fun loginDataChanged(email: String, password: String){
        if (!isValidEmail(email)) {
            loginForm.value = LoginFormState(usernameError = R.string.invalid_email)
        }
        else if (!isPasswordValid(password)) {
            loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        }
        else {
            loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    /**
     * Valid the e-mail address passed in param
     * @param email input email text
     * @return Boolean that indicate if e-mail is valid or not
     */
    private fun isValidEmail(email: String): Boolean{
        return when (true){
            email.contains("@") -> Patterns.EMAIL_ADDRESS.matcher(email).matches()
            else -> email.isBlank()
        }
    }

    /**
     * Valid the password passed in param with complex and strong validation
     * @param password input password text
     * @return Boolean that indicate if password is valid or not
     */
    private fun isPasswordValid(password: String): Boolean{
        //TODO: Split regex to trigger specific error
        return password.matches(Regex("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$"))
    }

}