package com.lab.zicevents.data.model.local

import com.google.firebase.auth.FirebaseUser

class LoginUserState(
    val user: FirebaseUser? = null,
    val error: Int? = null) {
}