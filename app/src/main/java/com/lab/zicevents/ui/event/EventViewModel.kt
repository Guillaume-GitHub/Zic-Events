package com.lab.zicevents.ui.event

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.type.LatLng
import com.lab.zicevents.R
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.api.songkick.SongkickRepository
import com.lab.zicevents.data.geolocation.FusedLocationRepository
import com.lab.zicevents.data.model.api.songkick.Event
import com.lab.zicevents.data.model.local.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventViewModel(
    private val songkickRepo: SongkickRepository,
    private val locationRepo: FusedLocationRepository
) : ViewModel() {

    private val events = MutableLiveData<DataResult>()

    fun observeEventsResult(): LiveData<DataResult> {
        return events
    }

    private val selected = MutableLiveData<Event>()

    fun select(event: Event) {
        selected.value = event
    }

    fun getSelectedItem(): LiveData<Event?>{
        return selected
    }

    private val _position = MutableLiveData<DataResult>()
    var position: LiveData<DataResult> =_position


    /**
     * Request api event to fetch all event nearby _position async
     * wait result and return DataResult object with ArrayList of Event object
     * (list must be empty) or error as int string
     * @param position LatLng object with latitude and longitude value
     * @param page optional Int that indicate the page of result you want to return (is 1 by default)
     */
    fun searchNearbyEvent(position: LatLng, page: Int? = null) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = songkickRepo.searchNearbyEvents(position, page)) {
                is Result.Success -> {
                    val eventList = result.data.resultsPage.results?.events
                    if (eventList != null) events.value = DataResult(data = eventList)
                    else events.value = DataResult(data = ArrayList<Event>())
                }
                is Result.Error -> DataResult(error = R.string.event_request_error)
                is Result.Canceled -> DataResult(error = R.string.operation_canceled)
            }
        }
    }

    /**
     * Request Last know device _position async
     * wait result and return DataResult object with Location? object
     * or error as int string
     * @param context context
     */
    fun getLastKnowPosition(context: Context?) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val positionResult = locationRepo.getLastKnowLocation(context)) {
                is Result.Success ->
                    _position.value = DataResult(data = transformLocationToLatlng(positionResult.data))
                is Result.Error ->
                    _position.value = DataResult(error = R.string.event_request_error)
                is Result.Canceled ->
                    _position.value = DataResult(error = R.string.operation_canceled)
            }
        }
    }

    
    /**
     * Transform a Location Object to LatLng object (nullable)
     * @return LatLng (nullable)
     */
    private fun transformLocationToLatlng(location: Location?): LatLng? {
        return if (location != null) {
            val latLng = LatLng.newBuilder()
            latLng.apply {
                latitude = location.latitude
                longitude = location.longitude
            }
            latLng.build()
        } else
            null
    }


    /**
     * Change string date to Date object
     */
    fun getFormattedDate(date: String?): Date? {
        val pattern = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(pattern, Locale.US)

        return if (date != null)
            dateFormat.parse(date)
        else
            null
    }
}
