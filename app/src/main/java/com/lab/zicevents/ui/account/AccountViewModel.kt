package com.lab.zicevents.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel() {

    private val text = MutableLiveData<String>().apply {
        value = "This is account Fragment"
    }
    val fragmentName: LiveData<String> = text
}