package com.lab.zicevents.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lab.zicevents.R
import com.lab.zicevents.data.api.songkick.SongkickApi
import com.lab.zicevents.data.model.api.songkick.Event
import com.lab.zicevents.utils.OnRecyclerItemClickListener
import kotlinx.android.synthetic.main.event_recycler_item.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventRecyclerAdapter(
    var events: ArrayList<Event>,
    var itemClickCallback: OnRecyclerItemClickListener? = null
) :
    RecyclerView.Adapter<EventRecyclerAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.event_recycler_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.updateView(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class EventViewHolder(var view: View) :
        RecyclerView.ViewHolder(view) {

        fun updateView(event: Event) {
            val performances = event.performance
            val artist = if (!performances.isNullOrEmpty()) performances[0]?.artist else null

            // Load Image / Set Background
            view.event_recycler_artist_image.apply {
                if (artist != null)
                    Glide.with(context)
                        .load("${SongkickApi.ARTIST_IMAGE_URL}${artist.id}/huge_avatar")
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(this)
                else
                    setImageDrawable(null)
            }

            // Set display Name
            view.event_recycler_artist_name.apply {
                text =
                    if (event.type == "Concert" && artist != null)
                        artist.displayName
                    else
                        event.displayName
            }

            // Set Event type background
            view.event_recycler_type.apply {

                text = event.type
                setBackgroundColor(
                    when (event.type) {
                        "Concert" -> ResourcesCompat.getColor(
                            resources,
                            android.R.color.holo_blue_dark,
                            null
                        )
                        "Festival" -> ResourcesCompat.getColor(
                            resources,
                            android.R.color.holo_orange_dark,
                            null
                        )
                        else -> ResourcesCompat.getColor(
                            resources,
                            android.R.color.transparent,
                            null
                        )
                    }
                )

            }

            // Set date + venue + address
            view.event_recycler_place.apply {
                val date = getFormattedDate(event.start.date)
                val venue = event.venue.displayName
                val location = event.location.city
                val formattedText =
                    if (date != null)
                        "Le ${DateFormat.getDateInstance().format(date)}, $venue, $location"
                    else
                        "$venue, $location"

                text = formattedText
            }

            // Set click listener
            view.setOnClickListener {
                itemClickCallback?.onItemClicked(adapterPosition)
            }
        }
    }

    /**
     * Change string date to Date object
     */
    private fun getFormattedDate(date: String?): Date? {
        val pattern = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(pattern, Locale.US)

        return if (date != null)
            dateFormat.parse(date)
        else
            null
    }
}