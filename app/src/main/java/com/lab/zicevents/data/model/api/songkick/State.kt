package com.lab.zicevents.data.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class State {

    @SerializedName("displayName")
    @Expose
    var displayName: String? = null

}