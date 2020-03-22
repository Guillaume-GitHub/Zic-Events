package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LocationSearch (
    @SerializedName("resultsPage")
    @Expose
    var page: Page
){
    inner class Page(
        @SerializedName("status")
        @Expose
        var status: String,
        @SerializedName("results")
        @Expose
        var results: Results
    )

    inner class Results(
        @SerializedName("location")
        @Expose
        var locationResults: ArrayList<LocationResults>?
    )

    inner class LocationResults(
        @SerializedName("city")
        @Expose
        var city: City,
        @SerializedName("metroArea")
        @Expose
        var metroArea: MetroArea
    )
}