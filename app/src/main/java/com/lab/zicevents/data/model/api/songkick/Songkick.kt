package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Songkick (

    @SerializedName("resultsPage")
    @Expose
    var resultsPage: ResultsPage

)