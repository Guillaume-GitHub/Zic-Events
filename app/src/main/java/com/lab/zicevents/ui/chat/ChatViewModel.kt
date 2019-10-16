package com.lab.zicevents.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {

    private val text = MutableLiveData<String>().apply {
        value = "This is chat Fragment"
    }
    val fragmentName: LiveData<String> = text
}