package com.lab.zicevents.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lab.zicevents.data.database.user.UserDataSource
import com.lab.zicevents.data.database.user.UserRepository

class SharedViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(UserRepository(UserDataSource())) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}