package com.lab.zicevents.ui.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.lab.zicevents.data.api.songkick.SongkickRepository

@Suppress("UNCHECKED_CAST")
class EventViewModelFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(
                songkickRepo = SongkickRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}