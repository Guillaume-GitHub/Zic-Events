package com.lab.zicevents.data.login

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.lab.zicevents.data.Result
import com.lab.zicevents.utils.base.BaseRepository

@Suppress("UNCHECKED_CAST")
class LoginRepository(private val loginDataSource: LoginDataSource): BaseRepository() {

    /**
     * @return LiveData<Boolean> that can be observe to trigger success / fail user creation
     */
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<AuthResult> {
        return when(val result = loginDataSource.createUserWithEmailAndPassword(email, password).awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Await Result of Firebase Task<> and return Result<AuthResult>
     * @param credential AuthCredential
     * @return Result<AuthResult> contain the result of Firebase sign in Task
     */
    suspend fun signInWithCredential(credential: AuthCredential): Result<AuthResult> {
        return when (val result = loginDataSource.signInWithCredential(credential).awaitTask()) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Await Result of Firebase Task<> sendPasswordResetEmail and return Result<Void>
     * @param email email address
     * @return Result<Void>
     */
    suspend fun sendPasswordEmail(email: String): Result<Void>{
        return when(val result = loginDataSource.sendPasswordResetEmail(email).awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }
}
