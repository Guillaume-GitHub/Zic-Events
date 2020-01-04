package com.lab.zicevents.data.model.api.geolocation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Viewport (
    @SerializedName("northeast")
    @Expose
    private var northeast: Northeast? = null,
    @SerializedName("southwest")
    @Expose
    private var southwest: Southwest? = null
)