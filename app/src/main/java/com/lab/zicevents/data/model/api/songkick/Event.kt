package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Event (

    @Expose
    @SerializedName("id")
    var id: Int,
    @SerializedName("type")
    @Expose
    var type: String,
    @SerializedName("uri")
    @Expose
    var uri: String,
    @SerializedName("displayName")
    @Expose
    var displayName: String,
    @SerializedName("start")
    @Expose
    var start: Start,
    @SerializedName("end")
    @Expose
    var end: End? = null,
    @SerializedName("performance")
    @Expose
    var performance: List<Performance?>,
    @SerializedName("location")
    @Expose
    var location: Location,
    @SerializedName("venue")
    @Expose
    var venue: Venue,
    @SerializedName("status")
    @Expose
    var status: String,
    @SerializedName("ageRestriction")
    @Expose
    var ageRestriction: String? = null

)