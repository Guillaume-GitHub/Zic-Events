package com.lab.zicevents.data.api.geocoding


import com.lab.zicevents.utils.base.BaseRepository
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.model.api.geocoding.Geocoding

class GeocodingRepository(private val geocodingDataSource: GeocodingDataSource): BaseRepository() {

    /**
     * Transform a retrofit Call<*> to Result<Geocoding> and return result async
     * @param address address taped by user
     * @param key Geocoding api key
     */
    suspend fun getGeolocationAddress(address: String): Result<Geocoding> {
        return when (val result = geocodingDataSource.getGeolocationAddress(address).awaitCall()){
           is Result.Success -> Result.Success(result.data)
           is Result.Error -> Result.Error(result.exception)
           is Result.Canceled -> Result.Canceled(result.exception)
       }
    }
}