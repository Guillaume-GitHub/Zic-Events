package com.lab.zicevents.ui.deal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_deal.*

class DealFragment : Fragment() {

    private lateinit var dealViewModel: DealViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_deal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initViewModel()
        this.bindView()
    }

    private fun initViewModel(){
        this.dealViewModel = ViewModelProviders.of(this).get(DealViewModel::class.java)
    }

    private fun bindView(){
        this.dealViewModel.fragmentName.observe(this, Observer { text ->
            text_deal.text = text
        })
    }
}