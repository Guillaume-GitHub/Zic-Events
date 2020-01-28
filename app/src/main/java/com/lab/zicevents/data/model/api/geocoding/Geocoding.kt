package com.lab.zicevents.data.model.api.geocoding

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Geocoding(
    @SerializedName("results")
    @Expose
    var results: List<Address>? = null
)
