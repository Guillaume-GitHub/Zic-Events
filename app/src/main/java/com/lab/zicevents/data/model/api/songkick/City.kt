package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class City {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("displayName")
    @Expose
    var displayName: String? = null
    @SerializedName("uri")
    @Expose
    var uri: String? = null
    @SerializedName("country")
    @Expose
    var country: Country? = null

}