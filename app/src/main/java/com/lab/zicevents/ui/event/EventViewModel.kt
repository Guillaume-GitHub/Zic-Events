package com.lab.zicevents.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.type.LatLng
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.api.songkick.SongkickRepository
import com.lab.zicevents.data.model.api.songkick.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EventViewModel(private val songkickRepo: SongkickRepository) : ViewModel() {

    private val events = MutableLiveData<ArrayList<Event>>()

    fun observeEventsResult(): LiveData<ArrayList<Event>> {
        return events
    }

    fun searchNearbyEvent(position: LatLng, page: Int? = null){
        GlobalScope.launch(Dispatchers.Main) {
            when(val result = songkickRepo.searchNearbyEvents(position, page)){
                is Result.Success -> {
                    val eventList = result.data.resultsPage?.results?.events
                    if(eventList != null) events.value = eventList
                    else events.value = ArrayList<Event>()
                }
            }
        }
    }
}
