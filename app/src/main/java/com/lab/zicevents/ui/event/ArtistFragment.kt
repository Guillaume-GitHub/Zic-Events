package com.lab.zicevents.ui.event

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.lab.zicevents.R
import com.lab.zicevents.data.api.songkick.SongkickRepository
import com.lab.zicevents.data.model.api.songkick.Artist
import com.lab.zicevents.data.model.api.songkick.Event
import com.lab.zicevents.utils.OnRecyclerItemClickListener
import com.lab.zicevents.utils.adapter.NetworkConnectivity
import com.lab.zicevents.utils.adapter.SimpleEventRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_artist.*

class ArtistFragment : Fragment(), OnRecyclerItemClickListener {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsAdapter: SimpleEventRecyclerAdapter
    private var eventsResults = ArrayList<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val artist = eventViewModel.getSelectedArtist().value

        if (artist != null) {
            configureRecyclerView()
            getArtistEvent(artist.id)
            updateUI(artist)
        } else {
            Toast.makeText(
                context,
                getText(R.string.artist_fragment_null_artist),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack(R.id.artist_fragment, true)
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
     * Bind all views
     * @param artist artist object containing information
     */
    private fun updateUI(artist: Artist) {
        loadImage(fragment_artist_image, SongkickRepository.getArtistImageUrl(artist.id))
        fragment_artist_name.text = artist.displayName
    }

    /**
     * Load image from url into image view
     * @param view image view destination
     * @param imageUrl url of image
     */
    private fun loadImage(view: AppCompatImageView, imageUrl: String) {
        Glide.with(this@ArtistFragment)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(view)
    }

    /**
     * Configure artist's Event recyclerView
     */
    private fun configureRecyclerView() {
        recyclerView = fragment_artist_recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        eventsAdapter = SimpleEventRecyclerAdapter(eventsResults, this)
        recyclerView.adapter = eventsAdapter
    }

    /**
     * Show Event details  fragment when item clicked
     */
    override fun onItemClicked(position: Int) {
        val event = eventsAdapter.artistEvents[position]
        eventViewModel.select(event)
        findNavController().navigate(ArtistFragmentDirections.fromArtistToEvent(event.id))
    }

    /**
     * Observe event result async and update adapter with result
     */
    @Suppress("UNCHECKED_CAST")
    private fun getArtistEvent(artistId: Int) {
        if (NetworkConnectivity.isOnline(context)) {
            eventViewModel.getArtistEvent(artistId)
            eventViewModel.artistEvents().observe(viewLifecycleOwner, Observer {
                fragment_artist_progress.visibility = View.GONE
                when {
                    it.data != null -> {
                        try {
                            changeDataSet((it.data as ArrayList<Event>))
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
                        changeDataSet(null)
                    }
                }
            })
        } else
            Toast.makeText(
                context,
                getText(R.string.no_network_connectivity),
                Toast.LENGTH_SHORT
            ).show()
    }

    /**
     * Get Event list and pass it to recycler adapter
     * notify adapter that data changed
     * Hide/Show empty result view if it's the case
     * @param eventList List of Event (nullable)
     */
    private fun changeDataSet(eventList: ArrayList<Event>?){
        eventsResults.clear()
        if (!eventList.isNullOrEmpty()) {
            eventsResults.addAll(eventList)
            showEmptyResultView(false)
        } else
            showEmptyResultView(true)

        eventsAdapter.notifyDataSetChanged()
    }

    /**
     * Hide/Show empty result view
     * @param display true = show, hide = false
     */
    private fun showEmptyResultView(display: Boolean){
        if (display) {
            fragment_artist_recyclerView.visibility = View.GONE
            fragment_artist_no_result.visibility = View.VISIBLE
        } else {
            fragment_artist_recyclerView.visibility = View.VISIBLE
            fragment_artist_no_result.visibility = View.GONE
        }
    }
}
