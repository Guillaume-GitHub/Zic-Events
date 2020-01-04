package com.lab.zicevents.data.api.geocoding

import com.google.type.LatLng
import com.lab.zicevents.data.model.api.geolocation.Geolocation
import retrofit2.Call

class GeocodingDataSource {

    private val service = GeocodingApi.create()

    fun getGeolocationFromLatlng(latLng: LatLng, key: String): Call<Geolocation> {
        return service.fetchGeolocationFromLatLng("${latLng.latitude},${latLng.longitude}","political" , key)
    }
}