package com.lab.zicevents.ui.event

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.type.LatLng

import com.lab.zicevents.R
import com.lab.zicevents.data.api.songkick.SongkickRepository
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EventFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initViewModel()
        this.bindView()
    }

    /**
     * Initialize viewModel
     */
    private fun initViewModel(){
        this.eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
    }

    /**
     * Update Update UI
     */
    private fun bindView(){
        this.eventViewModel.fragmentName.observe(viewLifecycleOwner, Observer {text ->
            fragment_event_location.text = text
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.event_search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search){
            findNavController().navigate(EventFragmentDirections.eventFragmentToSearchDialog())
        }
        return super.onOptionsItemSelected(item)
    }
}
