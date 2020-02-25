package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lab.zicevents.data.api.songkick.Location
import com.lab.zicevents.data.api.songkick.Performance
import com.lab.zicevents.data.api.songkick.Start
import com.lab.zicevents.data.api.songkick.Venue
import com.lab.zicevents.data.model.api.songkick.End

class Event {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("uri")
    @Expose
    var uri: String? = null
    @SerializedName("displayName")
    @Expose
    var displayName: String? = null
    @SerializedName("start")
    @Expose
    var start: Start? = null
    @SerializedName("end")
    @Expose
    var end: End? = null
    @SerializedName("performance")
    @Expose
    var performance: List<Performance>? = null
    @SerializedName("location")
    @Expose
    var location: Location? = null
    @SerializedName("venue")
    @Expose
    var venue: Venue? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("popularity")
    @Expose
    var popularity: Double? = null
    @SerializedName("ageRestriction")
    @Expose
    var ageRestriction: String? = null

}