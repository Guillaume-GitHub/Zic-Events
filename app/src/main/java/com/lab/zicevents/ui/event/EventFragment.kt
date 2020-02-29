package com.lab.zicevents.ui.event

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.type.LatLng

import com.lab.zicevents.R
import com.lab.zicevents.data.model.api.songkick.Event
import com.lab.zicevents.utils.MarginItemDecoration
import com.lab.zicevents.utils.adapter.EventRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_event.*

class EventFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventsRecycler: RecyclerView
    private lateinit var eventsAdapter: EventRecyclerAdapter
    private var eventsResults = ArrayList<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        initViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        observeEventsResult()
        this.bindView()
    }

    /**
     * Initialize viewModel
     */
    private fun initViewModel(){
        this.eventViewModel = ViewModelProviders.of(this, EventViewModelFactory())
            .get(EventViewModel::class.java)
    }

    /**
     * Update Update UI
     */
    private fun bindView(){

    }

    /**
     * Configure publication recyclerView
     */
    private fun configureRecyclerView(){
        eventsRecycler = fragment_event_recyclerView
        eventsRecycler.layoutManager = LinearLayoutManager(context)
        eventsAdapter = EventRecyclerAdapter(eventsResults)
        eventsRecycler.adapter = eventsAdapter

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

    /**
     * Observe event result async and update adapter with result
     */
    //Todo : no result view
    private fun observeEventsResult(){
        eventViewModel.observeEventsResult().observe(viewLifecycleOwner, Observer {
            eventsResults.addAll(it)
            eventsAdapter.notifyDataSetChanged()
        })
    }
}
