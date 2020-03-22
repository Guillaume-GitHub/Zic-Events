package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MetroArea (
    @SerializedName("uri")
    @Expose
    var uri: String,
    @SerializedName("displayName")
    @Expose
    var displayName: String,
    @SerializedName("country")
    @Expose
    var country: Country,
    @SerializedName("id")
    @Expose
    var id: Int,
    @SerializedName("state")
    @Expose
    var state: State? = null
)