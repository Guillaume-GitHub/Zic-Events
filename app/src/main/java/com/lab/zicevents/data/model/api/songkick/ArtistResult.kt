package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ArtistResult {

    @SerializedName("artist")
    @Expose
    var artists: ArrayList<Artist>? = null
    @SerializedName("totalEntries")
    @Expose
    var totalEntries: Int? = null
    @SerializedName("perPage")
    @Expose
    var perPage: Int? = null
    @SerializedName("page")
    @Expose
    var page: Int? = null
    @SerializedName("status")
    @Expose
    var status: Int? = null

}