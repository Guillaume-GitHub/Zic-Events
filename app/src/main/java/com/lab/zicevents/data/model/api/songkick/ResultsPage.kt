package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResultsPage (

    @SerializedName("status")
    @Expose
    var status: String,
    @SerializedName("results")
    @Expose
    var results: Results? = null,
    @SerializedName("perPage")
    @Expose
    var perPage: Int,
    @SerializedName("page")
    @Expose
    var page: Int,
    @SerializedName("totalEntries")
    @Expose
    var totalEntries: Int
)