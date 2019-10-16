package com.lab.zicevents.ui.deal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lab.zicevents.R

class DealFragment : Fragment() {

    private lateinit var dealViewModel: DealViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dealViewModel =
            ViewModelProviders.of(this).get(DealViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        dealViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}