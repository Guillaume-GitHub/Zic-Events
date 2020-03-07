package com.lab.zicevents.data.api.songkick

import com.lab.zicevents.BuildConfig
import com.lab.zicevents.data.model.api.songkick.DetailsEvent
import com.lab.zicevents.data.model.api.songkick.Songkick
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SongkickApiInterface {

    companion object {

        private const val SEARCH_EVENT_ENDPOINT = "events"
        private const val ARTIST_ENDPOINT = "artists"
        private const val SEARCH_ARTIST_ENDPOINT = "search/artists"
        private const val OUTPUT_FORMAT = "json"
        private const val KEY = BuildConfig.SONGKICK_KEY
    }

    /**
     * Search for upcoming artistEvents by location.
     */
    @GET("$SEARCH_EVENT_ENDPOINT.$OUTPUT_FORMAT?apikey=$KEY")
    fun getNearbyEvents(@Query("location") location: String,
                        @Query("page") page: Int): Call<Songkick>

    @GET("$SEARCH_EVENT_ENDPOINT/{eventId}.$OUTPUT_FORMAT?apikey=$KEY")
    fun getEventDetails(@Path("eventId") eventId: Int): Call<DetailsEvent>

    @GET("$ARTIST_ENDPOINT/{artist_id}/calendar.$OUTPUT_FORMAT?apikey=$KEY")
    fun getArtistCalendar(@Path("artist_id") artistId: Int,
                          @Query("page") page: Int): Call<Songkick>
}