package com.lab.zicevents.ui.publication


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.lab.zicevents.R
import kotlinx.android.synthetic.main.empty_publication_placeholder.*

/**
 * [Fragment] who display empty result like a placeholder.
 */
class EmptyPublicationPlaceholder : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.empty_publication_placeholder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Move to Search user fragment
        search_user_btn.setOnClickListener {
            findNavController().navigate(R.id.bottom_navigation_search)
        }
    }
}
