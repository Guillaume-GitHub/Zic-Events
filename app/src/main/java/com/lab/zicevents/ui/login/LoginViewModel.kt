package com.lab.zicevents.ui.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthException
import com.lab.zicevents.R
import com.lab.zicevents.data.login.LoginRepository
import com.lab.zicevents.data.Result
import kotlinx.coroutines.*

class LoginViewModel(private val loginRepository: LoginRepository): ViewModel() {
    
    private val TAG = this::class.java.simpleName

    private val loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = loginForm

    private val loginUser = MutableLiveData<LoginUserState>()
    val loginUserState: LiveData<LoginUserState> = loginUser

    /**
     * Launch Firebase sign in coroutine and pass the result to LiveData<LoginUserState> object
     * @param credential AuthCredential
     */
    fun signInWithCredential(credential: AuthCredential) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = loginRepository.signInWithFacebook(credential)) {
                is Result.Success -> {
                    val data = result.data
                    val user = data.user
                    loginUser.value = LoginUserState(user)
                }
                is Result.Error -> {
                    Log.e(TAG,"Sign In error : " , result.exception)
                    val exp = result.exception as FirebaseAuthException
                    when(exp.errorCode){
                        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                            loginUser.value = LoginUserState(error = R.string.email_used_by_other_provider)
                        }
                        "ERROR_USER_DISABLED" -> {
                            loginUser.value = LoginUserState(error = R.string.user_disable)
                        }
                        "ERROR_EMAIL_ALREADY_IN_USE" -> {
                            loginUser.value = LoginUserState(error = R.string.email_already_used)
                        }
                        else -> {
                            loginUser.value = LoginUserState(error = R.string.sign_in_fail)
                        }
                    }
                }
                is Result.Canceled -> loginUser.value = LoginUserState(error = R.string.sign_in_canceled)
            }
        }
    }

    /**
     * Send email and password to the repository to create new user with email and password (async task)
     * @return LiveData< Boolean> Observable boolean
     */
    fun createUserWithEmailAndPassword(email: String, password: String): LiveData< Boolean> {
        return  loginRepository.createUserWithEmailAndPassword(email, password)
    }

    /**
     * From data validation witch correct formatted email address and complex password
     * Change loginForm LiveData value
     * @param email email input text
     * @param password password input text
     */
    fun signUpFormDataChanged(email: String, password: String){
        if (!isValidEmail(email)) {
            loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    /**
     * From data validation witch correct formatted email address and not blank password
     * Change loginForm LiveData value
     * @param email email input text
     * @param password password input text
     */
    fun signInFormDataChanged(email: String, password: String){
        if (!isValidEmail(email)) {
            loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else if (password.isBlank()) {
            loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
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
        return password.matches(Regex("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_]).*$"))
    }
}

