package com.lab.zicevents.ui.event

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.type.LatLng
import com.lab.zicevents.R
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.api.songkick.SongkickRepository
import com.lab.zicevents.data.geolocation.FusedLocationRepository
import com.lab.zicevents.data.model.api.songkick.Artist
import com.lab.zicevents.data.model.api.songkick.Event
import com.lab.zicevents.data.model.api.songkick.MetroArea
import com.lab.zicevents.data.model.local.DataResult
import com.lab.zicevents.data.model.local.SearchLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventViewModel(
    private val songkickRepo: SongkickRepository,
    private val locationRepo: FusedLocationRepository
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        Log.d(this::class.java.simpleName, " OnCleared")
    }

    // General Events
    private val events = MutableLiveData<DataResult>()

    fun observeEventsResult(): LiveData<DataResult> {
        return events
    }

    // Artist Events
    private val artistEvents = MutableLiveData<DataResult>()

    fun artistEvents(): LiveData<DataResult> {
        return artistEvents
    }

    // Artists list result
    private val artists = MutableLiveData<DataResult>()

    fun artists(): LiveData<DataResult> {
        return artists
    }

    // Selected event data
    private val selected = MutableLiveData<Event>()

    fun select(event: Event) {
        selected.value = event
    }

    fun getSelectedItem(): LiveData<Event?> {
        return selected
    }

    // Selected artist data
    private val artist = MutableLiveData<Artist>()

    fun selectArtist(artist: Artist) {
        this.artist.value = artist
    }

    fun getSelectedArtist(): LiveData<Artist?> {
        return artist
    }

    // Device position Data
    private val _position = MutableLiveData<DataResult>()
    var position: LiveData<DataResult> = _position

    // Search location
    private val _location = MutableLiveData<String>()
    var searchLocationText: LiveData<String> = _location

    // Location list result
    private val locations = MutableLiveData<DataResult>()
    var locationList: LiveData<DataResult> = locations


    /**
     * Request api event to fetch all event nearby position
     * wait result and return ArrayList of Event object
     * (list must be empty) or error as int string
     * @param position LatLng object with latitude and longitude value
     * @param page optional Int that indicate the page of result you want to return (is 1 by default)
     */
    fun searchNearbyEvent(position: SearchLocation, page: Int? = null) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = songkickRepo.searchNearbyEvents(position, page)) {
                is Result.Success -> {
                    _location.value = position.displayName
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
     * Request api event to fetch all event nearby venue
     * wait result and return ArrayList of Event object
     * (list must be empty) or error as int string
     * @param metroArea its object who contains location information
     * @param page optional Int that indicate the page of result you want to return (is 1 by default)
     */
    fun searchNearbyEvent(metroArea: MetroArea, page: Int? = null) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = songkickRepo.searchNearbyEvents(metroArea.id, page)) {
                is Result.Success -> {
                    _location.value = metroArea.displayName
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
     * wait result and return DataResult object with SearchLocation? object
     * or error as int string
     */
    // TODO : PATCH CONTEXT MEMORY LEAKS
    fun getLastKnowPosition(context: Context?) {
        try {
            GlobalScope.launch(Dispatchers.Main) {
                when (val positionResult = locationRepo.getLastKnowLocation(context!!)) {
                    is Result.Success ->
                        _position.value =
                            DataResult(data = transformLocationToLatlng(positionResult.data))
                    is Result.Error ->
                        _position.value = DataResult(error = R.string.event_request_error)
                    is Result.Canceled ->
                        _position.value = DataResult(error = R.string.operation_canceled)
                }
            }
        } catch (e: KotlinNullPointerException) {
            Log.e(this::class.java.simpleName, "", e)
        }
    }

    /**
     * Request api event to fetch artist's upcoming artistEvents
     * @param artistId Id of artist
     * @param page Index of page result (1 by default)
     */
    fun getArtistEvent(artistId: Int, page: Int? = null) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = songkickRepo.getArtistCalendar(artistId, page)) {
                is Result.Success -> {
                    val eventList = result.data.resultsPage.results?.events
                    if (eventList != null) artistEvents.value = DataResult(data = eventList)
                    else artistEvents.value = DataResult(data = ArrayList<Event>())
                }
                is Result.Error -> DataResult(error = R.string.event_request_error)
                is Result.Canceled -> DataResult(error = R.string.operation_canceled)
            }
        }
    }

    /**
     * Search artists by user
     * @param query user query text
     * @param perPage nb result to return
     */
    fun getArtistByName(query: String, perPage: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = songkickRepo.getArtistByName(query, perPage)) {
                is Result.Success -> {
                    val artistList = result.data.resultsPage.results?.artist
                    if (artistList != null) artists.value = DataResult(data = artistList)
                    else artists.value = DataResult(data = ArrayList<Event>())
                }
                is Result.Error -> DataResult(error = R.string.artist_request_error)
                is Result.Canceled -> DataResult(error = R.string.operation_canceled)
            }
        }
    }

    /**
     * Search Location based on user query
     * @param query user query text
     * @param perPage nb result to return
     */
    fun getLocationByName(query: String, perPage: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = songkickRepo.getLocationByName(query, perPage)) {
                is Result.Success -> {
                    val locationResults = result.data.page.results.locationResults
                    if (locationResults != null) {
                        val metroAreaList: ArrayList<MetroArea> = ArrayList()
                        locationResults.forEach {
                            metroAreaList.add(it.metroArea)
                        }
                        locations.value = DataResult(data = metroAreaList)
                    }
                    else locations.value = DataResult(data = ArrayList<MetroArea>())
                }
                is Result.Error -> DataResult(error = R.string.artist_request_error)
                is Result.Canceled -> DataResult(error = R.string.operation_canceled)
            }
        }
    }

    /**
     * Transform a SearchLocation Object to LatLng object (nullable)
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
