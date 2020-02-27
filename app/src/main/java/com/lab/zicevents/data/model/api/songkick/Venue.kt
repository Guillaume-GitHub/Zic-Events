package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lab.zicevents.data.model.api.songkick.City

class Venue {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("displayName")
    @Expose
    var displayName: String? = null
    @SerializedName("uri")
    @Expose
    var uri: String? = null
    @SerializedName("city")
    @Expose
    var city: City? = null
    @SerializedName("lng")
    @Expose
    var lng: Double? = null
    @SerializedName("lat")
    @Expose
    var lat: Double? = null
    @SerializedName("street")
    @Expose
    var street: String? = null
    @SerializedName("zip")
    @Expose
    var zip: String? = null
    @SerializedName("phone")
    @Expose
    var phone: String? = null
    @SerializedName("website")
    @Expose
    var website: String? = null
    @SerializedName("capacity")
    @Expose
    var capacity: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("metroArea")
    @Expose
    var metroArea: MetroArea? = null

}