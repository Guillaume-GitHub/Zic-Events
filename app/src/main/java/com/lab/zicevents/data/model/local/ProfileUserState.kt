package com.lab.zicevents.data.model.local

import com.lab.zicevents.data.model.database.User

class ProfileUserState(val firestoreUser: User? = null, val error: Int? = null) {
}
