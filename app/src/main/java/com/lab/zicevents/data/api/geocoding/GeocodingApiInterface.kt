package com.lab.zicevents.data.api.geocoding

import com.lab.zicevents.data.model.api.geolocation.GeoResults
import com.lab.zicevents.data.model.api.geolocation.Geolocation
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiInterface {

    companion object {
        private const val OUTPUT_FORMAT = "json"
    }

    @GET("maps/api/geocode/$OUTPUT_FORMAT")
    fun fetchGeolocationFromAddress(@Query("address") address: String, @Query("key") key: String): Deferred<Response<GeoResults>>

    @GET("maps/api/geocode/$OUTPUT_FORMAT")
    fun fetchGeolocationFromLatLng(@Query("latlng") latLng: String,
                                   @Query("result_type") resultType: String,
                                   @Query("key") key: String): Call<Geolocation>
}