package com.lab.zicevents.data.login

import androidx.lifecycle.LiveData

class LoginRepository(private val loginDataSource: LoginDataSource) {

    /**
     * @return LiveData<Boolean> that can be observe to trigger success / fail signIn
     */
    fun signInWithEmailAndPassword(email: String, password: String): LiveData<Boolean> {
        return  loginDataSource.signInWithEmailAndPassword(email, password)
    }

    /**
     * @return LiveData<Boolean> that can be observe to trigger success / fail user creation
     */
    fun createUserWithEmailAndPassword(email: String, password: String): LiveData<Boolean> {
        return  loginDataSource.createUserWithEmailAndPassword(email, password)
    }
}