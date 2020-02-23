package com.lab.zicevents.data.api.geocoding

import com.lab.zicevents.BuildConfig
import com.lab.zicevents.data.model.api.geocoding.Geocoding
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiInterface {

    companion object {
        private const val OUTPUT_FORMAT = "json"
        private const val KEY = BuildConfig.GEOCODING_KEY
    }

    @GET("maps/api/geocode/$OUTPUT_FORMAT?key=$KEY")
    fun fetchGeolocationFromAddress(@Query("address") address: String): Call<Geocoding>

    @GET("maps/api/geocode/$OUTPUT_FORMAT?key=$KEY")
    fun fetchGeolocationFromLatLng(@Query("latlng") latLng: String): Call<Geocoding>
}