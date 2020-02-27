package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Performance {

    @SerializedName("artist")
    @Expose
    var artist: Artist? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("displayName")
    @Expose
    var displayName: String? = null
    @SerializedName("billingIndex")
    @Expose
    var billingIndex: Int? = null
    @SerializedName("billing")
    @Expose
    var billing: String? = null

}