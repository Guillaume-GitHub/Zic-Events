package com.lab.zicevents.data.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginDataSource(firebaseAuth: FirebaseAuth? = null) {

    private val TAG = this.javaClass.simpleName

    // Firebase Auth instance
    private val auth = firebaseAuth ?: FirebaseAuth.getInstance()

    /**
     * Try to create user with his email and password
     * @param email input email string
     * @param password input password string
     * @return LiveData<Boolean>
     */
    fun createUserWithEmailAndPassword(email: String, password: String) : Task<AuthResult> {
        Log.d(TAG, "Create account for : $email")
        return auth.createUserWithEmailAndPassword(email, password)
    }

    /**
     * Try to signIn user with a auth credential
     * @param credential AuthCredential
     * @return Firebase Task<AuthResult>
     */
   fun signInWithCredential(credential: AuthCredential): Task<AuthResult> {
        Log.d(TAG, "Sign In with : ${credential.provider}")
        return auth.signInWithCredential(credential)
    }

    /**
     * Send email with link to reset user password
     * @param email is address email
     * @return Task<Void>
     */
    fun sendPasswordResetEmail(email: String): Task<Void> {
        Log.d(TAG, "Send password reset email to $email")
        return auth.sendPasswordResetEmail(email)
    }
}