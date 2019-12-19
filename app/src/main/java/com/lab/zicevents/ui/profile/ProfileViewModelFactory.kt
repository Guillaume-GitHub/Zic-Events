package com.lab.zicevents.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lab.zicevents.data.database.UserDataSource
import com.lab.zicevents.data.database.UserRepository

/**
 * ViewModel provider factory instantiate LoginViewModel.
 */

class ProfileViewModelFactory: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                userRepository = UserRepository(
                    userDataSource = UserDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
