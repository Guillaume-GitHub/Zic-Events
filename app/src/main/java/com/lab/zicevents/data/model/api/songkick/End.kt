package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class End {

    @SerializedName("time")
    @Expose
    var time: String? = null
    @SerializedName("date")
    @Expose
    var date: String? = null
    @SerializedName("datetime")
    @Expose
    var datetime: String? = null

}