package com.lab.zicevents.data.model.local

import com.lab.zicevents.data.model.database.user.User

class InsertProfileState(
    val isUserCreated: Boolean = false,
    val isPrivateInfoCreated: Boolean = false,
    val error: Int? = null) {
}
