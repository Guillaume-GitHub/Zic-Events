package com.lab.zicevents.data.model.api.geolocation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



data class AddressComponent(
    @SerializedName("long_name")
    @Expose
    private var longName: String? = null,
    @SerializedName("short_name")
    @Expose
    private var shortName: String? = null,
    @SerializedName("types")
    @Expose
    private var types: List<String>? = null
)