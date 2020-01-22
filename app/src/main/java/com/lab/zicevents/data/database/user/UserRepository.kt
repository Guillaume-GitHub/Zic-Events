package com.lab.zicevents.data.database.user

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.lab.zicevents.data.Result
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
     * Get Firestore user from Firestore
     * @param documentRef String that corresponding to the path of document in database
     * @return Result<DocumentSnapshot>
     */
    suspend fun getUserByReference(documentRef : String) : Result<DocumentSnapshot>{
        return when (val result = userDataSource.getUserByDocReference(documentRef).awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Get User realtime updates
     * @param uid string corresponding to user uid
     * @return DocumentReference
     */
    fun listenUserUpdate(uid: String): DocumentReference {
       return userDataSource.listenUserUpdate(uid)
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
     * @param value is User object corresponding to new user data
     * @return Result<Void>
     */
    suspend fun updateUserProfile(uid : String, map: Map<String, Any?>) : Result<Void>{
        return when (val result = userDataSource.updateUserProfile(uid, map).awaitTask()){
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