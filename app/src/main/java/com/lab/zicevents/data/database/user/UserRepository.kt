package com.lab.zicevents.data.database.user

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.database.user.UserDataSource
import com.lab.zicevents.data.model.database.user.PrivateUserInfo
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.utils.base.BaseRepository

class UserRepository(private val userDataSource: UserDataSource) : BaseRepository(){

    /**
     * Create Firestore user and transform Task to Kotlin Coroutine
     * @param user User.class that corresponding to user information's
     * @return Result<Void>
     */
    suspend fun createFirestoreUser(user: User) : Result<Void>{
        return when (val result = userDataSource.createFirestoreUser(user).awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Create private user info and transform Task to Kotlin Coroutine
     * @param userId id of authenticated user
     * @param privateInfo object who contain private user info
     * @return Result<Void>
     */
    suspend fun createPrivateUserInfo(userId: String, privateInfo: PrivateUserInfo) : Result<Void>{
        return when (val result = userDataSource.createPrivateUserInfo(userId, privateInfo).awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Get Firestore user and transform Task to Kotlin Coroutine
     * @param uid String that corresponding to user uid
     * @return Result<DocumentSnapshot>
     */
    suspend fun getFirestoreUser(uid : String) : Result<DocumentSnapshot>{
        return when (val result = userDataSource.getFirestoreUser(uid).awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Get all Firestore user and transform Task to Kotlin Coroutine
     * @return Result<QuerySnapshot>
     */
    suspend fun getAllFirestoreUsers() : Result<QuerySnapshot>{
        return when (val result = userDataSource.getAllFirestoreUsers().awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Update Firestore user and transform Task to Kotlin Coroutine
     * @param uid String that corresponding to user uid
     * @param fields Map<String, Any> that corresponding to user fields that you want to update
     * @return Result<Void>
     */
    suspend fun updateFirestoreUser(uid : String, fields: Map<String, Any>) : Result<Void>{
        return when (val result = userDataSource.updateFirestoreUser(uid, fields).awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Delete Firestore user and transform Task to Kotlin Coroutine
     * @param uid String that corresponding to user uid
     * @return Result<Void>
     */
    suspend fun deleteFirestoreUser(uid : String) : Result<Void>{
        return when (val result = userDataSource.deleteFirestoreUser(uid).awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

}