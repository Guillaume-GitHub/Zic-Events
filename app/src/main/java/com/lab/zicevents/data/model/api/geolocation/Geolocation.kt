package com.lab.zicevents.data.model.api.geolocation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GeoResult(
    @SerializedName("results")
    @Expose
    var results: List<GeoResult>? = null
)
