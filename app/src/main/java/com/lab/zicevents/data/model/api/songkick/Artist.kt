package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class Artist (

    @SerializedName("uri")
    @Expose
    var uri: String,
    @SerializedName("displayName")
    @Expose
    var displayName: String,
    @SerializedName("id")
    @Expose
    var id: Int,
    @SerializedName("identifier")
    @Expose
    var identifier: List<Any?>,
    @SerializedName( "onTourUntil")
    @Expose
    var onTourUntil: Date? = null

)