package com.lab.zicevents.ui.publication


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.lab.zicevents.R
import com.lab.zicevents.activity.MainActivity
import com.lab.zicevents.data.model.local.DataResult
import com.lab.zicevents.utils.OnActivityFabClickListener
import com.lab.zicevents.utils.OnPublicationClickListener
import com.lab.zicevents.utils.base.BaseRepository
import kotlinx.android.synthetic.main.empty_publication_placeholder.*

/**
 * [Fragment] who display empty result like a placeholder.
 */
class EmptyPublicationPlaceholder : Fragment(), OnActivityFabClickListener {

    private lateinit var publicationViewModel: PublicationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? MainActivity)?.registerFabClickCallback(this)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.empty_publication_placeholder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Move to Search user fragment
        search_user_btn.setOnClickListener {
            findNavController().navigate(R.id.bottom_navigation_search)
        }

        publicationViewModel.publicationCreationSate.observe(viewLifecycleOwner, Observer {

            if (it.data == BaseRepository.SUCCESS_TASK) {
                findNavController().popBackStack()
                findNavController().navigate(R.id.bottom_navigation_publication)
            }

            if (it.error != null) Toast.makeText(
                context,
                getText(it.error),
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    /**
     * Init Publication ViewModel
     */
    private fun initViewModel() {
        this.publicationViewModel = ViewModelProviders
            .of(activity!!, PublicationViewModelFactory())
            .get(PublicationViewModel::class.java)
    }

    override fun onFabClick() {
        val ft = fragmentManager?.beginTransaction()
        ft?.let {
            AddPublicationFragmentDialog(publicationViewModel).show(ft, "AddPublicationDialog")
        }
    }

}
