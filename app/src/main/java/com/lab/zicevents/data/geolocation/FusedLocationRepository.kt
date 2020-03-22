package com.lab.zicevents.data.geolocation

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.lab.zicevents.data.Result
import com.lab.zicevents.utils.base.BaseRepository

class FusedLocationRepository : BaseRepository() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /**
     * Get All User's Publications. Transform Task to Kotlin Coroutine
     * @param userId String that corresponding to user uid
     * @return Result<SearchLocation?>
     */
    suspend fun getLastKnowLocation(context: Context): Result<Location?> {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return when (val result = fusedLocationClient.lastLocation.awaitTask()) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }
}