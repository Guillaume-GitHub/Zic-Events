package com.lab.zicevents.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lab.zicevents.R
import com.lab.zicevents.data.api.songkick.SongkickRepository
import com.lab.zicevents.data.model.api.songkick.Artist
import com.lab.zicevents.data.model.api.songkick.Location
import com.lab.zicevents.data.model.api.songkick.MetroArea
import com.lab.zicevents.utils.OnRecyclerItemClickListener
import kotlinx.android.synthetic.main.simple_event_item.view.*
import kotlinx.android.synthetic.main.simple_list_artist_item.view.*
import kotlinx.android.synthetic.main.simple_text_item.view.*
import kotlin.collections.ArrayList

class LocationRecyclerAdapter(
    var locationList: ArrayList<MetroArea>,
    var itemClickCallback: OnRecyclerItemClickListener? = null
) : RecyclerView.Adapter<LocationRecyclerAdapter.SimpleTextViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleTextViewHolder {
        return SimpleTextViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.simple_text_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SimpleTextViewHolder, position: Int) {
        holder.updateView(locationList[position])
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    inner class SimpleTextViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun updateView(metroArea: MetroArea) {

            view.textView.apply {
                this.text = metroArea.displayName
            }

            itemClickCallback?.let { callback ->
                view.rootView.setOnClickListener {
                    callback.onItemClicked(adapterPosition)
                }
            }
        }
    }
}