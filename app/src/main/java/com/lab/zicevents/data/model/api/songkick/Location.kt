package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Location {

    @SerializedName("city")
    @Expose
    var city: String? = null
    @SerializedName("lng")
    @Expose
    var lng: Double? = null
    @SerializedName("lat")
    @Expose
    var lat: Double? = null

}