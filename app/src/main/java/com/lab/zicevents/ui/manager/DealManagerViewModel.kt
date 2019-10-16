package com.lab.zicevents.ui.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DealManagerViewModel : ViewModel() {

    private val text = MutableLiveData<String>().apply {
        value = "This is deal manager Fragment"
    }

    val fragmentName: LiveData<String> = text
}
