package com.lab.zicevents.data.api.geocoding

import com.google.type.LatLng
import com.lab.zicevents.data.model.api.geolocation.Geolocation
import com.lab.zicevents.utils.base.BaseRepository
import com.lab.zicevents.data.Result


class GeocodingRepository(val geocodingDataSource: GeocodingDataSource): BaseRepository() {

    suspend fun getGeolocationFromLatlng(latLng: LatLng, key: String): Result<Geolocation> {
        return when(val result = geocodingDataSource.getGeolocationFromLatlng(latLng, key).awaitCall()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

/*
    suspend fun getGeolocationFromLatlng(latLng: LatLng, key: String): Geolocation? {
        var resukt: Geolocation? = null
        Log.d("TAG", "laaaaaaaaaaaaaaaa")
        geocodingDataSource.getGeolocationFromLatlng(latLng, key)
            .enqueue(object : Callback<Geolocation> {

                override fun onResponse(call: Call<Geolocation>, response: Response<Geolocation>) {
                    if (response.isSuccessful) {
                        val resp = response.body()
                        resukt = resp
                        Log.d("ALL", resp.toString())
                        Log.d("adr", resp?.results?.get(0)?.formattedAddress.toString())
                    }


                }

                override fun onFailure(call: Call<Geolocation>, t: Throwable) {
                    Log.e("ERROR","",t)
                }
            })
        return resukt
    }
 */
}