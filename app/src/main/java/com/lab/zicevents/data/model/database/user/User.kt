package com.lab.zicevents.data.model.database.user

import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.storage.StorageReference
import com.lab.zicevents.data.model.api.geocoding.Address
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
    var address: Address? = null,
    var profileImage: String? = null,
    var coverImage: String? = null,
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
        null,
        null,
        null
    )

    companion object {
        const val PSEUDO_FIELD = "pseudo"
        // Fields Can be updated
        const val DISPLAY_NAME_FIELD = "displayName"
        const val DESCRIPTION_FIELD = "description"
        const val PROFILE_IMAGE_FIELD = "profileImage"
        const val COVER_IMAGE_FIELD = "coverImage"
        const val GALLERY_FIELD = "gallery"
        const val MUSIC_STYLE_FIELD = "musicStyle"
        const val ADDRESS_FIELD = "address"
    }
}