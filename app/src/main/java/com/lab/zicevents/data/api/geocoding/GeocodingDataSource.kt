package com.lab.zicevents.data.api.geocoding

import com.google.type.LatLng
import com.lab.zicevents.data.model.api.geocoding.Geocoding
import retrofit2.Call

class GeocodingDataSource {

    private val service = GeocodingApi.create()

    fun getGeolocationFromLatlng(latLng: LatLng): Call<Geocoding> {
        return service.fetchGeolocationFromLatLng("${latLng.latitude},${latLng.longitude}")
    }

    /**
     * Perform Http request to fetch address components from string address
     * @param address address taped by user
     * @param key Geocoding api key
     * @return retrofit2 Call<Geocoding>
     */
    fun getGeolocationAddress(address: String): Call<Geocoding> {
        return service.fetchGeolocationFromAddress(address)
    }
}