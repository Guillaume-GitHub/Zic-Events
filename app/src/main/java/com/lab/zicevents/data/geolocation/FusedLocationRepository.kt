package com.lab.zicevents.data.geolocation

import android.app.Activity
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
     * @return Result<DocumentSnapshot>
     */
   suspend fun getLastKnowLocation(activity: Activity) : Result<Location?> {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        return when (val result = fusedLocationClient.lastLocation.awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }
}