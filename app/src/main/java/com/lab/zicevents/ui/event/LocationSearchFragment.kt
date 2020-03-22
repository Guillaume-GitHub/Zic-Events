package com.lab.zicevents.ui.event

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.lab.zicevents.R
import com.lab.zicevents.activity.MainActivity
import com.lab.zicevents.data.model.api.songkick.Location
import com.lab.zicevents.data.model.api.songkick.MetroArea
import com.lab.zicevents.utils.OnActivityFabClickListener
import com.lab.zicevents.utils.OnRecyclerItemClickListener
import com.lab.zicevents.utils.adapter.LocationRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_search_dialog.*

class LocationSearchFragment : DialogFragment(), OnRecyclerItemClickListener{

    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: LocationRecyclerAdapter
    private var locationList = ArrayList<MetroArea>()
    private var handler: Handler = Handler()
    private var runnable: Runnable? = null
    private val MAX_RESULT = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME,R.style.AppTheme_FullScreenDialog)
        retainInstance = true
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        observeLocationSearchResults()

        fragment_search_dialog_searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank() && query.length >= 3)
                    getLocation(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // remove runnable callback if present
                runnable?.let {
                    handler.removeCallbacks(it)
                }
                // delay artist search
                if (!newText.isNullOrBlank() && newText.length >= 3) {
                    val r = Runnable { getLocation(newText) }
                    runnable = r
                    handler.postDelayed(r, 500)
                }
                return false
            }
        })

        fragment_search_dialog_searchView.apply {
            queryHint = getText(R.string.location_search_query_hint)
        }

        fragment_search_dialog_toolbar.setNavigationOnClickListener {
            this.dismiss()
        }
    }

    /**
     * Initialize viewModel
     */
    private fun initViewModel() {
        eventViewModel = ViewModelProviders.of(activity!!, EventViewModelFactory())
            .get(EventViewModel::class.java)
    }

    /**
     * Configure recyclerView
     */
    private fun configureRecyclerView() {
        fragment_search_dialog_recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = LocationRecyclerAdapter(locationList, this)
        fragment_search_dialog_recyclerView.adapter = adapter
    }

    /**
     * Hide/Show recycler view
     * @param boolean True = shown, False = hide
     */
    private fun showRecyclerView(boolean: Boolean){
        fragment_search_dialog_recyclerView.apply {
            visibility = if (boolean)
                View.VISIBLE
            else
                View.GONE
        }
    }

    /**
     * Hide/Show message
     * @param boolean True = shown, False = hide
     */
    private fun showMessage(boolean: Boolean, message: String? = null){
        fragment_search_dialog_message.apply {
            if (boolean) {
                visibility = View.VISIBLE
                text = message ?: ""
            }
            else
                View.GONE
        }
    }

    /**
     * Get location based on user query search
     * @param query user query string
     */
    private fun getLocation(query: String) {
        eventViewModel.getLocationByName(query, MAX_RESULT)
        fragment_search_dialog_progress.visibility = View.VISIBLE
    }

    /**
     * Observe artist search result
     */
    private fun observeLocationSearchResults() {
        eventViewModel.locationList.observe(viewLifecycleOwner, Observer {
            fragment_search_dialog_progress.visibility = View.GONE

            when {
                it.data is List<*> -> {
                    val list: List<MetroArea> = it.data.filterIsInstance(MetroArea::class.java)
                    updateUI(ArrayList(list))
                }
                it.error is Int -> Toast
                    .makeText(context, getText(it.error), Toast.LENGTH_SHORT)
                    .show()
                else -> {}
            }
        })
    }

    /**
     * Update recycler view or display message
     * @param metroAreaList list of metroArea corresponding to query search (nullable)
     */
    private fun updateUI(metroAreaList: ArrayList<MetroArea>?){
        locationList.clear()

        if (!metroAreaList.isNullOrEmpty()){
            fragment_search_dialog_message.visibility = View.GONE
            locationList.addAll(metroAreaList)
            // show recycler
            showRecyclerView(true)
            // Hide message
            showMessage(false)
        } else {
            // display message
            val message = getText(R.string.location_search_no_result)
            showMessage(true, message.toString())
            // Hide Recycler view
            showRecyclerView(false)
        }

        adapter.notifyDataSetChanged()
    }

    /**
     * Recycler item clicked callback
     * @param position item position in adapter
     */
    override fun onItemClicked(position: Int) {
        eventViewModel.searchNearbyEvent(adapter.locationList[position])
        this.dismiss()
    }
}
