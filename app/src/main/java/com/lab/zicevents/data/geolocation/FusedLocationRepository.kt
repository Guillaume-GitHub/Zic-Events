package com.lab.zicevents.data.geolocation

import android.app.Activity
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.lab.zicevents.data.Result
import com.lab.zicevents.utils.base.BaseRepository

class FusedLocationRepository: BaseRepository() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    /**
     * Get All User's Publications. Transform Task to Kotlin Coroutine
     * @param userId String that corresponding to user uid
     * @return Result<Location?>
     */
   suspend fun getLastKnowLocation(activity: Activity) : Result<Location?> {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        return when (val result = fusedLocationClient.lastLocation.awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Get All User's Publications. Transform Task to Kotlin Coroutine
     * @param userId String that corresponding to user uid
     * @return Result<Location?>
     */
    suspend fun getLastKnowLocation(context: Context?) : Result<Location?> {
        if (context != null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            return when (val result = fusedLocationClient.lastLocation.awaitTask()) {
                is Result.Success -> Result.Success(result.data)
                is Result.Error -> Result.Error(result.exception)
                is Result.Canceled -> Result.Canceled(result.exception)
            }
        }
        else throw Throwable("Context must not be null")
    }
}