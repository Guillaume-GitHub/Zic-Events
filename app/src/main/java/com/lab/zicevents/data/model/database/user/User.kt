package com.lab.zicevents.data.model.database.user

import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.storage.StorageReference
import java.lang.ref.Reference
import java.util.*
import kotlin.collections.ArrayList

/**
 * Data class who corresponding to User Firestore model
 */
data class User(
    @ServerTimestamp
    val createdDate: Date? = null,
    val userId: String,
    val docRef: String,
    var displayName: String,
    var pseudo: String,
    var description: String? = null,
    var profileImage: String? = null,
    var gallery: ArrayList<String>? = null,
    var musicStyle: ArrayList<String>? = null)  {

    // Empty constructor *Required to deserialize datas fetched from firestore
    constructor(): this (
        null,
        "",
        "",
        "",
        "",
        null,
        null,
        null,
        null
    )

    companion object {
        // Fields Can be updated
        const val DISPLAY_NAME_FIELD = "displayName"
        const val DESCRIPTION_FIELD = "description"
        const val PROFILE_IMAGE_FIELD = "profileImage"
        const val GALLERY_FIELD = "gallery"
        const val MUSIC_STYLE_FIELD = "musicStyle"
    }
}