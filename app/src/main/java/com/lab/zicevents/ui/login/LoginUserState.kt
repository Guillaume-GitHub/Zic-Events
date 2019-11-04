package com.lab.zicevents.ui.login

import com.google.firebase.auth.FirebaseUser

class LoginUserState(
    val user: FirebaseUser? = null,
    val error: Int? = null) {
}