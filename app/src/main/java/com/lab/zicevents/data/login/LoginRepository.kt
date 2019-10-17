package com.lab.zicevents.data.login

import androidx.lifecycle.LiveData

class LoginRepository(private val loginDataSource: LoginDataSource) {

    fun login(email: String, password: String): LiveData<Boolean> {
        return  loginDataSource.login(email, password)
    }
}