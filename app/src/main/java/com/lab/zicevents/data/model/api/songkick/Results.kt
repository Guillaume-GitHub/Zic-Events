package com.lab.zicevents.data.model.api.songkick

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Results (

    @SerializedName("event")
    @Expose
    var events: ArrayList<Event>? = null,
   // @SerializedName("event")
  //  @Expose
  //  var event: Event? = null,
    @SerializedName("artist")
    @Expose
    var artist: ArrayList<Artist>? = null
)