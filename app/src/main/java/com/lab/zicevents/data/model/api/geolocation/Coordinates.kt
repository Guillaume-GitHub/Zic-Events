package com.lab.zicevents.data.model.api.geolocation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Coordinates(

    @SerializedName("location")
    @Expose
    private var position: Position? = null,
    @SerializedName("viewport")
    @Expose
    private var viewport: Viewport? = null
)