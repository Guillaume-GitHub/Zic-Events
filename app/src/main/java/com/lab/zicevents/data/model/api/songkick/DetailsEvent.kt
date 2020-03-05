package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DetailsEvent (
    @SerializedName("resultsPage")
    @Expose
    var page: Page
) {
    inner class Page(
        @SerializedName("status")
        @Expose
        var status: String,
        @SerializedName("results")
        @Expose
        var result: Result
    )

    inner class Result(
        @SerializedName("event")
        @Expose
        var event: Event
    )
}