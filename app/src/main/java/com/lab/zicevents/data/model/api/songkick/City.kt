package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class City (

    @SerializedName("id")
    @Expose
    var id: Int,
    @SerializedName("displayName")
    @Expose
    var displayName: String,
    @SerializedName("uri")
    @Expose
    var uri: String,
    @SerializedName("country")
    @Expose
    var country: Country

)