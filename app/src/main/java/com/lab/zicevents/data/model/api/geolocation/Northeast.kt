package com.lab.zicevents.data.model.api.geolocation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Northeast (

    @SerializedName("lat")
    @Expose
    private var lat: Double? = null,
    @SerializedName("lng")
    @Expose
    private var lng: Double? = null
)