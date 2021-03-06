package com.lab.zicevents.data.database.user

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.lab.zicevents.data.model.database.user.PrivateUserInfo
import com.lab.zicevents.data.model.database.user.User

/**
 * CRUD Firebase Cloud Firestore User
 */
class UserDataSource(firestore: FirebaseFirestore? = null) {

    private val TAG = this::class.java.simpleName
    private val settings = FirebaseFirestoreSettings.Builder()
        .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
        .build()
    private val database =
        firestore ?: FirebaseFirestore.getInstance()

    companion object {
        const val USERS_COLLECTION = "users"
        const val PRIVATE_COLLECTION = "private"
    }


    init {
        database.firestoreSettings = settings
    }

    //****************** CREATE *********************//
    /**
     * Create Firestore user document
     * @param user User.class corresponding to user information's
     * @return Task<DocumentSnapshot>
     */
    fun createFirestoreUser(user: User): Task<Void> {
        return database.collection(USERS_COLLECTION).document(user.userId).set(user)
    }

    /**
     * Create Firestore user private document
     * @param privateInfo PrivateUserInfo.class corresponding to private user information's
     * @return Task<DocumentSnapshot>
     */
    fun createPrivateUserInfo(userId: String, privateInfo: PrivateUserInfo): Task<Void> {
        return database.collection(USERS_COLLECTION).document(userId)
            .collection(PRIVATE_COLLECTION).document().set(privateInfo)
    }
    //****************** READ *********************//
    /**
     * Get specific Firestore user document
     * @param uid string corresponding to user uid
     * @return Task<DocumentSnapshot>
     */
    fun getFirestoreUser(uid: String): Task<DocumentSnapshot> {
        Log.d(TAG, "Get user $uid")
        return database.collection(USERS_COLLECTION).document(uid).get()
    }

    /**
     * Get Firestore user from Firestore
     * @param documentRef String that corresponding to the path of document in database
     * @return Task<DocumentSnapshot>
     */
    fun getUserByDocReference(documentRef: String): Task<DocumentSnapshot> {
        return database.document(documentRef).get()
    }

    /**
     * Get User realtime updates
     * @param uid string corresponding to user uid
     * @return DocumentReference
     */
    fun listenUserUpdate(uid: String): DocumentReference {
        return database.collection(USERS_COLLECTION).document(uid)
    }

    /**
     * Get all Firestore users document
     * @return Task<QuerySnapshot>
     */
    fun getAllFirestoreUsers(): Task<QuerySnapshot> {
        Log.d(TAG, "Get all users")
        return database.collection(USERS_COLLECTION).get()
    }

    /**
     * Search users by her displayName or pseudo
     * @param query displayName or pseudo filter text
     * @return Task<QuerySnapshot>
     */
    fun searchUsers(query: String): Task<QuerySnapshot> {
        // Field where search value
        val filter = if (query.startsWith("#")) User.PSEUDO_FIELD else User.DISPLAY_NAME_FIELD
        //
        return database.collection(USERS_COLLECTION)
            .whereGreaterThanOrEqualTo(filter, query)
            .whereLessThanOrEqualTo(filter, query + '\uf8ff')
            .orderBy(filter)
            .limit(10)
            .get()
    }

    /**
     * Return Document Reference Object
     * @param documentPath path of document in database or userId
     */
    fun getUserDocRef(documentPath: String): DocumentReference {
        return database.collection(USERS_COLLECTION).document(documentPath)
    }

    //****************** UPDATE *********************//
    /**
     * Update specific Firestore user document
     * @param uid string corresponding to user uid
     * @param fields map<String, Any> corresponding to User fields you want to change
     * @return Task<Void>
     */
    fun updateUserProfile(uid: String, map: Map<String, Any?>): Task<Void> {
        return database.collection(USERS_COLLECTION).document(uid).update(map)
    }

    //****************** DELETE *********************//
    /**
     * Delete specific Firestore user document
     * @param uid string corresponding to user uid
     * @return Task<Void>
     */
    fun deleteFirestoreUser(uid: String): Task<Void> {
        return database.collection(USERS_COLLECTION).document(uid).delete()
    }
}