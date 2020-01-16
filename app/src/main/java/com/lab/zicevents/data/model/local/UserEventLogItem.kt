package com.lab.zicevents.data.model.local

import java.util.*

class UserEventLogItem(val userId: String,
                       val userImage: String?,
                       val createdDate: Date,
                       val imageUrl:String?,
                       val message: String,
                       val modificationType: String) {
}