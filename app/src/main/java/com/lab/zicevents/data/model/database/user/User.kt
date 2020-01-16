package com.lab.zicevents.data.model.database.user

import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

/**
 * Data class who corresponding to User Firestore model
 */
data class User(
    @ServerTimestamp
    val createdDate: Date? = null,
    val userId: String,
    var displayName: String,
    var pseudo: String,
    var description: String? = null,
    var photoURL: String? = null,
    var gallery: ArrayList<String>? = null,
    var musicStyle: ArrayList<String>? = null)  {

    // Empty constructor *Required to deserialize datas fetched from firestore
    constructor(): this (
        null,
        "",
        "",
        "",
        null,
        null,
        null,
        null
    )
}