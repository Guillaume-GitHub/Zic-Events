package com.lab.zicevents.data.model.database

/**
 * Data class who corresponding to User Firestore model
 */
data class User(val userId : String, var displayName: String, var photoURL: String?,var phoneNumber: String?, var categoryId: Int) {
    // Empty constructor *Required to deserialize datas fetched from firestore
    constructor() :this("","", null,null,-1)
}