package com.lab.zicevents.ui.deal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DealViewModel : ViewModel() {

    private val text = MutableLiveData<String>().apply {
        value = "This is deal Fragment"
    }

    val fragmentName: LiveData<String> = text
}