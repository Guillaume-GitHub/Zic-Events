package com.lab.zicevents.data.login

import androidx.lifecycle.LiveData
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

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

    /**
     * @return LiveData<Boolean> that can be observe to trigger success / fail Google signIn
     */
    fun signInWithGoogle(account: GoogleSignInAccount): LiveData<Boolean> {
        return loginDataSource.signInhWithGoogle(account)
    }

    /**
     * @return LiveData<Boolean> that can be observe to trigger success / fail facebook signIn
     */
    fun signInWithFacebook(token: AccessToken): LiveData<Boolean> {
        return loginDataSource.signInWithFacebook(token)
    }
}