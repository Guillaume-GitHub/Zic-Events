package com.lab.zicevents.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventViewModel : ViewModel() {

    private val text = MutableLiveData<String>().apply {
        value = "This is events Fragment"
    }

    val fragmentName: LiveData<String> = text
}
