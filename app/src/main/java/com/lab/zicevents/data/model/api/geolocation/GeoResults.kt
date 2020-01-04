package com.lab.zicevents.data.model.api.geolocation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class GeoResults (
    @SerializedName("address_components")
    @Expose
    var addressComponents: List<AddressComponent>? = null,
    @SerializedName("formatted_address")
    @Expose
    var formattedAddress: String? = null,
    @SerializedName("geometry")
    @Expose
    var coordinates: Coordinates? = null
)