package com.lab.zicevents.data.login

import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.lab.zicevents.data.Result
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("UNCHECKED_CAST")
class LoginRepository(private val loginDataSource: LoginDataSource) {

    /**
     * @return LiveData<Boolean> that can be observe to trigger success / fail user creation
     */
    fun createUserWithEmailAndPassword(email: String, password: String): LiveData<Boolean> {
        return loginDataSource.createUserWithEmailAndPassword(email, password)
    }

    /**
     * Await Result of Firebase Task<> and return Result<AuthResult>
     * @param credential AuthCredential
     * @return Result<AuthResult> contain the result of Firebase sign in Task
     */
    suspend fun signInWithFacebook(credential: AuthCredential): Result<AuthResult> {
        return when (val result = loginDataSource.signInWithCredential(credential).await()) {
            is Result.Success -> {
                Result.Success(result.data)
            }
            is Result.Error -> {
                Result.Error(result.exception)
            }
            is Result.Canceled -> {
                Result.Canceled(result.exception)
            }
        }
    }


    /**
     * Task extension to transform Firebase Task<> to Kotlin suspendCoroutine
     */
    suspend fun <T> Task<T>.await(): Result<T> = suspendCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val e = exception
                if (e == null) {
                    if (isCanceled) continuation.resumeWithException(CancellationException("Task $this was cancelled normally."))
                    else continuation.resume(Result.Success(result as T))
                } else {
                    continuation.resume(Result.Error(e))
                }
            } else{
                continuation.resume(Result.Error(exception!!))
            }
        }
    }
}
