package com.lab.zicevents.data.database

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.lab.zicevents.data.model.User

/**
 * CRUD Firebase Cloud Firestore User
 */
class UserDataSource {

    private val TAG = this::class.java.simpleName
    private val database = FirebaseFirestore.getInstance()
    private val collection = "users"


    //****************** CREATE *********************//
    /**
     * Create Firestore user document
     * @param user User.class corresponding to user information's
     * @return Task<DocumentSnapshot>
     */
    fun createFirestoreUser(user: User) : Task<Void> {
        return database.collection(collection).document(user.userId).set(user)
    }

    //****************** READ *********************//
    /**
     * Get specific Firestore user document
     * @param uid string corresponding to user uid
     * @return Task<DocumentSnapshot>
     */
    fun getFirestoreUser(uid: String) : Task<DocumentSnapshot> {
        Log.d(TAG, "Get user $uid")
        return database.collection(collection).document(uid).get()
    }

    /**
     * Get all Firestore users document
     * @return Task<QuerySnapshot>
     */
    fun getAllFirestoreUsers() : Task<QuerySnapshot> {
        Log.d(TAG, "Get all users")
        return database.collection(collection).get()
    }

    //****************** UPDATE *********************//
    /**
     * Update specific Firestore user document
     * @param uid string corresponding to user uid
     * @param fields map<String, Any> corresponding to User fields you want to change
     * @return Task<Void>
     */
    fun updateFirestoreUser(uid: String, fields: Map<String, Any>) : Task<Void> {
        return database.collection(collection).document(uid).update(fields)
    }

    //****************** DELETE *********************//
    /**
     * Delete specific Firestore user document
     * @param uid string corresponding to user uid
     * @return Task<Void>
     */
    fun deleteFirestoreUser(uid: String) : Task<Void>{
        return database.collection(collection).document(uid).delete()
    }
}