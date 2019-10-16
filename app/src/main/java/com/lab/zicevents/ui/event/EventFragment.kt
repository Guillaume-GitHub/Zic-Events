package com.lab.zicevents.ui.event

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_event.*

class EventFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initViewModel()
        this.bindView()
    }

    private fun initViewModel(){
        this.eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
    }

    private fun bindView(){
        this.eventViewModel.fragmentName.observe(this, Observer {text ->
            text_event.text = text
        })
    }

}
