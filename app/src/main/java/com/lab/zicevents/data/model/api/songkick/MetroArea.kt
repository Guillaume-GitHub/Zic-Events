package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MetroArea {

    @SerializedName("uri")
    @Expose
    var uri: String? = null
    @SerializedName("displayName")
    @Expose
    var displayName: String? = null
    @SerializedName("country")
    @Expose
    var country: Country? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("state")
    @Expose
    var state: State? = null

}