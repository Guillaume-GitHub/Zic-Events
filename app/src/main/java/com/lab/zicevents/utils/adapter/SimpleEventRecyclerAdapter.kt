package com.lab.zicevents.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lab.zicevents.R
import com.lab.zicevents.data.model.api.songkick.Event
import com.lab.zicevents.utils.OnRecyclerItemClickListener
import kotlinx.android.synthetic.main.simple_event_item.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SimpleEventRecyclerAdapter(
    var artistEvents: ArrayList<Event>,
    var itemClickCallback: OnRecyclerItemClickListener? = null
) : RecyclerView.Adapter<SimpleEventRecyclerAdapter.ArtistEventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistEventViewHolder {
        return ArtistEventViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.simple_event_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ArtistEventViewHolder, position: Int) {
        holder.updateView(artistEvents[position])
    }

    override fun getItemCount(): Int {
        Log.d("Recycler items count", artistEvents.size.toString())
        return artistEvents.size
    }

    inner class ArtistEventViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun updateView(event: Event) {
            view.simple_event_item_date.apply {
                val date = getFormattedDate(event.start.date)
                if (date != null)
                    text = DateFormat.getDateInstance().format(date)
                else
                    visibility = View.GONE
            }
            view.simple_event_item_name.text = event.venue.displayName
            view.simple_event_item_location.text = event.location.city

            itemClickCallback?.let { callback ->
                view.simple_event_item_root.setOnClickListener {
                    callback.onItemClicked(adapterPosition)
                }
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