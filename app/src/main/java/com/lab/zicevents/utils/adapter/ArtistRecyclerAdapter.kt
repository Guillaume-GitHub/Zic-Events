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
import com.lab.zicevents.utils.OnRecyclerItemClickListener
import kotlinx.android.synthetic.main.simple_event_item.view.*
import kotlinx.android.synthetic.main.simple_list_artist_item.view.*
import kotlin.collections.ArrayList

class ArtistRecyclerAdapter(
    var artistList: ArrayList<Artist>,
    var itemClickCallback: OnRecyclerItemClickListener? = null
) : RecyclerView.Adapter<ArtistRecyclerAdapter.SimpleTextViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleTextViewHolder {
        return SimpleTextViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.simple_list_artist_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SimpleTextViewHolder, position: Int) {
        holder.updateView(artistList[position])
    }

    override fun getItemCount(): Int {
        return artistList.size
    }

    inner class SimpleTextViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun updateView(artist: Artist) {

            loadImage(
                view.simple_list_artist_item_image,
                SongkickRepository.getArtistImageUrl(artist.id)
            )

            view.simple_list_artist_item_name.apply {
                this.text = artist.displayName
            }
            itemClickCallback?.let { callback ->
                view.simple_list_artist_item_root.setOnClickListener {
                    callback.onItemClicked(adapterPosition)
                }
            }
        }

        /**
         * Try to download image via Url and bind image view
         * @param url complete http url
         * @param view imageView where to load image
         */
        private fun loadImage(view: ImageView, url: String) {
            Glide.with(view)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.color.colorPrimaryLight)
                .into(view)
        }

    }
}