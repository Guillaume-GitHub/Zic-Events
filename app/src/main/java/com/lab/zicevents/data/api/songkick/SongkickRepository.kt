package com.lab.zicevents.data.api.songkick

import com.google.type.LatLng
import com.lab.zicevents.utils.base.BaseRepository
import retrofit2.Call
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.model.api.songkick.DetailsEvent
import com.lab.zicevents.data.model.api.songkick.Event
import com.lab.zicevents.data.model.api.songkick.Songkick

class SongkickRepository: BaseRepository() {

    private val service = SongkickApi.create()

    companion object {
        fun getArtistImageUrl(artistId: Int): String{
           return "https://images.sk-static.com/images/media/profile_images/artists/$artistId/huge_avatar"
        }
    }

    //******************************** EVENTS ****************************************

    /**
     * Fetch song event next to position
     * Wait http request result async
     * @param location Latlng object containing latitude and longitude
     * @param page Int corresponding to the page to return
     */
    suspend fun searchNearbyEvents(location: LatLng, page: Int? = null): Result<Songkick> {
        return when (val result = getNearbyEvents(location, page).awaitCall()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Fetch song event next to position
     * @param location coordinates of user
     * @param page page result index (default is 1)
     */
    private fun getNearbyEvents(location: LatLng, page: Int?): Call<Songkick> {
        val index = page ?: 1
        val formattedLoc = "geo:${location.latitude},${location.longitude}"
        return service.getNearbyEvents(location = formattedLoc, page = index)
    }


    /**
     * Fetch Event details
     * Wait http request result async
     * @param eventId event id as int
     */
    suspend fun getEventDetails(eventId: Int): Result<Event> {
        return when (val result = fetchEventDetails(eventId).awaitCall()){
            is Result.Success -> Result.Success(result.data.page.result.event)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Fetch specific event details
     * @param eventId event id as int
     */
    private fun fetchEventDetails(eventId: Int): Call<DetailsEvent> {
        return service.getEventDetails(eventId)
    }


    //******************************** ARTIST ****************************************

    /**
     * Fetch Artist's upcoming artistEvents
     * Wait http request result async
     * @param artistId Artist id
     * @param page index of page result (1 by default)
     */
    suspend fun getArtistCalendar(artistId: Int, page: Int? = null): Result<Songkick> {
        return when (val result = fetchArtistEvent(artistId, page).awaitCall()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Fetch Artist corresponding to query
     * @param artistName artist name query
     * @param page page result index (default is 1)
     */
    private fun fetchArtistEvent(artistName: Int, page: Int?): Call<Songkick> {
        val index = page ?: 1
        return service.getArtistCalendar(artistName, index)
    }
}