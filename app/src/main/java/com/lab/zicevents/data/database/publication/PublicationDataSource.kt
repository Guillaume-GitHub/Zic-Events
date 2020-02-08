package com.lab.zicevents.data.database.publication

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.data.model.database.user.User

/**
 *  CRUD Firebase Publication collection
 */
class PublicationDataSource {

    private val database = FirebaseFirestore.getInstance()
    private val COLLECTION = "publications"
    private val USER_ID_FIELD = "userId"

    //****************** CREATE *********************//
    /**
     * Create new publication document
     * @param publication Publication.class corresponding to publication information's
     * @return Task<DocumentSnapshot>
     */
    fun createPublication(publication: Publication) : Task<Void> {
        return database.collection(COLLECTION).document().set(publication)
    }

    //****************** READ *********************//
    /**
     * Get All user publications
     * @param userId string corresponding to user uid
     * @return Task<DocumentSnapshot>
     */
    fun getUserPublications(userId: String) : Task<QuerySnapshot> {
        return database.collection(COLLECTION).whereEqualTo(USER_ID_FIELD, userId).get()
    }

    /**
     * Get All users subscriptions publications
     * @param subscriptionList list of user id where request filter
     * @return Task<DocumentSnapshot>
     */
    fun getSubscribedPublications(subscriptionList: List<String>): Task<QuerySnapshot> {
        return database.collection(COLLECTION)
            .whereIn(User.ID_FIELD, subscriptionList)
            .orderBy(Publication.DATE_FIELD, Query.Direction.DESCENDING)
            .get()
    }

    //****************** UPDATE *********************//
    /**
     * Update publication document
     * @param docId string corresponding to user uid
     * @param fields map<String, Any> corresponding to publication fields you want to change
     * @return Task<Void>
     */
    fun updatePublication(docId: String, fields: Map<String, Any>) : Task<Void> {
        return database.collection(COLLECTION).document(docId).update(fields)
    }

    //****************** DELETE *********************//
    /**
     * Delete publication document
     * @param docId string corresponding to user uid
     * @return Task<Void>
     */
    fun deletePublication(docId: String) : Task<Void>{
        return database.collection(COLLECTION).document(docId).delete()
    }
}