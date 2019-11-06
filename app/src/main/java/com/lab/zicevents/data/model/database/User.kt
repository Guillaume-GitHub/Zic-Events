package com.lab.zicevents.data.model.database

/**
 * Data class who corresponding to User Firestore model
 */
data class User(val userId : String, val userName: String) {
    // Empty constructor *Required to deserialize datas fetched from firestore
    constructor() :this("","")
}