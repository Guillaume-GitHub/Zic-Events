package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Performance (

    @SerializedName("artist")
    @Expose
    var artist: Artist,
    @SerializedName("id")
    @Expose
    var id: Int,
    @SerializedName("displayName")
    @Expose
    var displayName: String,
    @SerializedName("billingIndex")
    @Expose
    var billingIndex: Int,
    @SerializedName("billing")
    @Expose
    var billing: String

)