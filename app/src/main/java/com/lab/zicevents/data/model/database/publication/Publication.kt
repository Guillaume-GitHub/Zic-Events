package com.lab.zicevents.data.model.database.publication

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ServerTimestamp
import com.lab.zicevents.data.model.database.user.User
import java.util.*

class Publication (
    @ServerTimestamp
    val createdDate: Date? = null,
    val userId: String,
    var mediaUrl: String?,
    var message: String,
    var user: DocumentReference?)  {

    // Empty constructor *Required to deserialize datas fetched from firestore
    constructor(): this (
    null,
    "",
    null,
    "",
        null)
}