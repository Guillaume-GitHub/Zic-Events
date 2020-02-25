package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class Artist {

    @SerializedName("uri")
    @Expose
    var uri: String? = null
    @SerializedName("displayName")
    @Expose
    var displayName: String? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("identifier")
    @Expose
    var identifier: List<Any>? = null
    @SerializedName( "onTourUntil")
    @Expose
    var onTourUntil: Date? = null

}