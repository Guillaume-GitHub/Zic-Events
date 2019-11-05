package com.lab.zicevents.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lab.zicevents.data.database.UserDataSource
import com.lab.zicevents.data.database.UserRepository
import com.lab.zicevents.data.login.LoginDataSource
import com.lab.zicevents.data.login.LoginRepository

/**
 * ViewModel provider factory instantiate LoginViewModel.
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginRepository = LoginRepository(
                    loginDataSource =  LoginDataSource()
                ),
                userRepository = UserRepository(
                    userDataSource = UserDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
