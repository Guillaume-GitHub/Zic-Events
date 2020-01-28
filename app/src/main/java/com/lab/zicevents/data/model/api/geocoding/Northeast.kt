package com.lab.zicevents.data.model.api.geocoding

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Northeast (

    @SerializedName("lat")
    @Expose
    var lat: Double? = null,
    @SerializedName("lng")
    @Expose
    var lng: Double? = null
)