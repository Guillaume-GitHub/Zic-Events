package com.lab.zicevents.ui.event

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.type.LatLng

import com.lab.zicevents.R
import com.lab.zicevents.activity.MainActivity
import com.lab.zicevents.data.model.api.songkick.Event
import com.lab.zicevents.data.model.local.SearchLocation
import com.lab.zicevents.utils.OnActivityFabClickListener
import com.lab.zicevents.utils.OnRecyclerItemClickListener
import com.lab.zicevents.utils.OnRequestPermissionsListener
import com.lab.zicevents.utils.PermissionHelper
import com.lab.zicevents.utils.adapter.EventRecyclerAdapter
import com.lab.zicevents.utils.adapter.NetworkConnectivity
import kotlinx.android.synthetic.main.fragment_event.*

class EventFragment : Fragment(), OnRecyclerItemClickListener, OnRequestPermissionsListener , OnActivityFabClickListener{

    private var firstInit = false
    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventsRecycler: RecyclerView
    private lateinit var eventsAdapter: EventRecyclerAdapter
    private var eventsResults = ArrayList<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? MainActivity)?.registerFabClickCallback(this)
        setHasOptionsMenu(true)
        initViewModel()
        firstInit = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        observeEventsResult()
        observeSearchLocation()

        if (firstInit) {
            getLastKnowLocation()
            firstInit = false
        }

        // Observe dataset result and show message when empty
        eventsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                if (eventsAdapter.events.isEmpty()) {
                    fragment_event_recyclerView.visibility = View.GONE
                    fragment_event_no_result.visibility = View.VISIBLE
                } else {
                    fragment_event_recyclerView.visibility = View.VISIBLE
                    fragment_event_no_result.visibility = View.GONE
                }
            }
        })
    }

    /**
     * Initialize viewModel
     */
    private fun initViewModel() {
        this.eventViewModel = ViewModelProviders.of(activity!!, EventViewModelFactory())
            .get(EventViewModel::class.java)
    }

    private fun getLastKnowLocation() {
        val permsResult =
            PermissionHelper().checkPermissions(context!!, PermissionHelper.LOCATION_PERMISSIONS)
        // Ask permission to user if permission denied
        if (permsResult.isNullOrEmpty()) {
            observePositionOnce()
            if (NetworkConnectivity.isConnected())
                eventViewModel.getLastKnowPosition(context) // get Last know device position
            else
                Toast.makeText(
                    context,
                    getText(R.string.no_network_connectivity),
                    Toast.LENGTH_SHORT
                ).show()

        } else { // ASk permissions to user
            val activity = activity
            PermissionHelper().askRequestPermissions(
                activity,
                PermissionHelper.LOCATION_PERMISSIONS, this
            )
        }
    }

    /**
     * Update UI
     */
    private fun updatePlaceText(location: String?) {
        fragment_event_location.apply {
            if (!location.isNullOrBlank()) {
                text = location
                visibility = View.VISIBLE
            } else {
                text = null
                visibility = View.GONE
            }
        }
    }

    /**
     * Configure publication recyclerView
     */
    private fun configureRecyclerView() {
        eventsRecycler = fragment_event_recyclerView
        eventsRecycler.layoutManager = LinearLayoutManager(context)
        eventsAdapter = EventRecyclerAdapter(eventsResults, this)
        eventsRecycler.adapter = eventsAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.event_search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_search ->
                findNavController().navigate(EventFragmentDirections.eventFragmentToSearchDialog())
            R.id.action_myPosition ->
                getLastKnowLocation()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Fetch Nearby artistEvents async
     * show progress bar
     * @param position SearchLocation object with latitude and longitude
     */
    private fun fetchEvents(position: SearchLocation) {
        if (NetworkConnectivity.isConnected()) {
            fragment_event_progress.visibility = View.VISIBLE
            eventViewModel.searchNearbyEvent(position)
        }
        else
            Toast.makeText(
                context,
                getText(R.string.no_network_connectivity),
                Toast.LENGTH_SHORT
            ).show()
    }

    /**
     * Observe event result async and update adapter with result
     */
    @Suppress("UNCHECKED_CAST")
    private fun observeEventsResult() {
        eventViewModel.observeEventsResult().observe(viewLifecycleOwner, Observer {
            fragment_event_progress.visibility = View.GONE
            when {
                it.data != null -> {
                    try {
                        eventsResults.clear()
                        eventsResults.addAll((it.data as ArrayList<Event>))
                        eventsAdapter.notifyDataSetChanged()
                    } catch (castError: ClassCastException) {
                        Log.e(this::class.java.simpleName, "", castError)
                    }
                }
                it.error != null -> {
                    Toast.makeText(
                        context,
                        getText(it.error),
                        Toast.LENGTH_LONG
                    ).show()
                    eventsResults.clear()
                    eventsAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    /**
     * Observe Device position result then fetch event around this position
     * or display error message
     */
    private fun observePositionOnce() {//
        eventViewModel.position.observe(viewLifecycleOwner, Observer {
            eventViewModel.position.removeObservers(viewLifecycleOwner) // remove observer
            when {
                it.data is LatLng -> {
                    // Get events
                    fetchEvents(
                        SearchLocation(  // Fetch nearby artistEvents
                            displayName = getString(R.string.nearby_position_hint),
                            latitude = it.data.latitude,
                            longitude = it.data.longitude
                        )
                    )
                }
                it.data is LatLng? -> displayErrorMessage(R.string.device_position_error)
                it.error is Int -> displayErrorMessage(it.error)
            }
        })
    }

    /**
     * Observe search location text change
     */
    private fun observeSearchLocation(){
        eventViewModel.searchLocationText.observe(viewLifecycleOwner, Observer {
            updatePlaceText(it)
        })
    }

    /**
     * On item click, show event details fragment
     */
    override fun onItemClicked(position: Int) {
        val event = eventsAdapter.events[position]
        eventViewModel.select(event)
        val action = EventFragmentDirections.fromEventToEventDetail(event.id)
        findNavController().navigate(action)
    }

    /**
     * Show Toast message
     * @param message message from in string.xml
     */
    private fun displayErrorMessage(message: Int) {
        Toast.makeText(
            context,
            getText(message),
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Get request permission result
     */
    override fun onRequestPermissions(isGranted: Boolean, grantResult: Map<String, Int>) {
        if (isGranted)
            getLastKnowLocation()
        else
            Toast.makeText(
                context,
                getString(R.string.location_permission_denied),
                Toast.LENGTH_LONG
            ).show()
    }

    override fun onFabClick() {
        val action = EventFragmentDirections.eventFragmentToLocationSearchDialog()
        findNavController().navigate(action)
    }
}
