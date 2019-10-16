package com.lab.zicevents.ui.manager

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_deal_manager.*

class DealManagerFragment : Fragment() {

    private lateinit var dealManagerViewModel: DealManagerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_deal_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initViewModel()
        this.bindView()
    }

    private fun initViewModel(){
        this.dealManagerViewModel = ViewModelProviders.of(this).get(DealManagerViewModel::class.java)
    }

    private fun bindView(){
        this.dealManagerViewModel.fragmentName.observe(this, Observer { text ->
            text_deal_management.text = text
        })
    }
}
