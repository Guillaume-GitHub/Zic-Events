package com.lab.zicevents.ui.event

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.lab.zicevents.R
import com.lab.zicevents.data.api.maps.StaticMap
import com.lab.zicevents.data.api.songkick.SongkickRepository
import com.lab.zicevents.data.model.api.songkick.Artist
import com.lab.zicevents.data.model.api.songkick.Event
import com.lab.zicevents.data.model.api.songkick.Venue
import kotlinx.android.synthetic.main.fragment_event_detail.*
import kotlinx.android.synthetic.main.simple_list_artist_item.view.*
import java.text.DateFormat
import android.content.Intent
import android.net.Uri


//TODO:
class EventDetailFragment : Fragment() {
    private lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Observe Event result
        eventViewModel.getSelectedItem().observe(viewLifecycleOwner, Observer {
            it?.let { event ->
                updateUI(event)
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

    /**
     * Bind all view
     */
    private fun updateUI(event: Event) {
        // Load Image
        fragment_event_detail_image.apply {
            val artist = if (event.performance.isNotEmpty()) event.performance[0]?.artist else null
            if (artist != null)
                loadImage(this, SongkickRepository.getArtistImageUrl(artist.id))
        }

        // Event Name
        fragment_event_detail_event_name.apply {
            text =
                if (event.type == "Concert") event.performance[0]?.displayName else event.displayName
        }

        // Is event available
        fragment_event_detail_status_container.apply {
            if (event.status == "ok")
                visibility = View.GONE
        }

        // Date View
        fragment_event_detail_event_date.apply {
            val date = eventViewModel.getFormattedDate(event.start.date)
            if (date != null)
                text = DateFormat.getDateInstance().format(date)
            else
                visibility = View.GONE
        }

        // Add Artist list
        event.performance.forEach { performance ->
            performance?.let {
                val params =
                    LinearLayout.LayoutParams(fragment_event_detail_artist_list.layoutParams)
                params.setMargins(0, 0, 0, 20)
                fragment_event_detail_artist_list.addView(
                    createPerformanceView(it.artist), params
                )
            }
        }

        // Venue elements
        bindVenue(event.venue)
    }

    /**
     * Bind or Hide venue section view
     * display informations about venue and get static map corresponding to position
     * Start google maps on click
     * @param venue venue object containing venue's informations
     */
    private fun bindVenue(venue: Venue) {

        if (venue.lng != null && venue.lat != null) {
            fragment_event_detail_location_title.text = venue.displayName

            val view = fragment_event_detail_location_image
            val url = StaticMap.getUrlStaticMap(
                venue.lng!!,
                venue.lat!!,
                true,
                15,
                resources.displayMetrics.widthPixels,
                view.layoutParams.height
            )
            // Load image
            loadImage(fragment_event_detail_location_image, url)

            // Start google maps with route to event destination
            fragment_event_detail_location_route.setOnClickListener {
                val uri = Uri.parse("geo:${venue.lat},${venue.lng}?q=${venue.lat},${venue.lng}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }

        } else {
            fragment_event_detail_location.visibility = View.GONE
            fragment_event_detail_location_container.visibility = View.GONE
        }
    }

    /**
     * Load image from url into image view
     * @param view image view destination
     * @param imageUrl url of image
     */
    private fun loadImage(view: AppCompatImageView, imageUrl: String) {
        Glide.with(this@EventDetailFragment)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(view)
    }

    /**
     *  Create child into LinearLayout dynamically and bind views
     *  Each element represent an artist object
     *  @param artist artit object containing artist information
     */
    @SuppressLint("InflateParams")
    private fun createPerformanceView(artist: Artist): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.simple_list_artist_item, null)
        loadImage(
            view.simple_list_artist_item_image,
            SongkickRepository.getArtistImageUrl(artist.id)
        )
        view.simple_list_artist_item_name.text = artist.displayName
        view.setOnClickListener {
            Toast.makeText(context, artist.displayName, Toast.LENGTH_SHORT).show()
        }
        return view
    }
}
