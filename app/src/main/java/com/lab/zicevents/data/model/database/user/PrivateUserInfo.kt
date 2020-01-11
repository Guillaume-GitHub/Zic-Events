package com.lab.zicevents.data.model.database.user

import java.util.*

class PrivateUserInfo(
    var gender: String,
    var firstName: String? = null,
    var lastName: String? = null,
    var birthDate: Date? = null,
    var email: String,
    var phoneNumber: String? = null) {

    // Empty constructor Required to deserialize datas fetched from firestore
    constructor(): this(
        "",
        null,
        null,
        null,
        "",
        null)
}