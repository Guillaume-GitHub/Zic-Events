package com.lab.zicevents.data.api.songkick

import com.google.type.LatLng
import com.lab.zicevents.utils.base.BaseRepository
import retrofit2.Call
import com.lab.zicevents.data.Result
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

    //******************************** ARTIST ****************************************

    /**
     * Fetch Artist corresponding to user query
     * Wait http request result async
     * @param artistName Artist name corresponding to user query
     */
    suspend fun searchArtistByName(artistName: String): Result<Songkick> {
        return when (val result = getArtistByName(artistName).awaitCall()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Fetch Artist corresponding to query
     * @param artistName artist name query
     */
    private fun getArtistByName(artistName: String): Call<Songkick> {
        return service.getArtistByName(artistName)
    }
}