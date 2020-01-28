package com.lab.zicevents.data.api.geocoding

import com.lab.zicevents.data.model.api.geocoding.Geocoding
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiInterface {

    companion object {
        private const val OUTPUT_FORMAT = "json"
    }

    @GET("maps/api/geocode/$OUTPUT_FORMAT")
    fun fetchGeolocationFromAddress(@Query("address") address: String,
                                    @Query("key") key: String): Call<Geocoding>

    @GET("maps/api/geocode/$OUTPUT_FORMAT")
    fun fetchGeolocationFromLatLng(@Query("latlng") latLng: String,
                                   @Query("key") key: String): Call<Geocoding>
}