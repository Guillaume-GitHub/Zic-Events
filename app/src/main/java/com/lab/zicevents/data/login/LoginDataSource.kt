package com.lab.zicevents.data.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginDataSource {

    private val TAG = this.javaClass.simpleName

    // Firebase Auth instance
    private val auth = FirebaseAuth.getInstance()

    /**
     * Try to create user with his email and password
     * @param email input email string
     * @param password input password string
     * @return LiveData<Boolean>
     */
    fun createUserWithEmailAndPassword(email: String, password: String) : LiveData<Boolean>{
        val createSuccessful = MutableLiveData<Boolean>()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, " createUserWithEmail:success")
                    createSuccessful.value = true
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, " createUserWithEmail:failure", task.exception)
                    createSuccessful.value = true
                }
            }

        return createSuccessful
    }

    /**
     * Try to signIn user with a auth credential
     * @param credential AuthCredential
     * @return LiveData<Boolean>//todo gfh
     */
   fun signInWithCredential(credential: AuthCredential): Task<AuthResult> {
        Log.d(TAG, "Sign In with : ${credential.provider}")
        return auth.signInWithCredential(credential)
    }
}