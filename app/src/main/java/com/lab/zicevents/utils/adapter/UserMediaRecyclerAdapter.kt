package com.lab.zicevents.utils.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lab.zicevents.R
import kotlinx.android.synthetic.main.picture_gallery_recycler_item.view.*

class UserMediaRecyclerAdapter(val context: Context, var mediaUrlList: ArrayList<String>)
    : RecyclerView.Adapter<UserMediaRecyclerAdapter.MediaHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaHolder {
        return MediaHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.picture_gallery_recycler_item, parent,false))
    }

    override fun onBindViewHolder(holder: MediaHolder, position: Int) {
        holder.bindItem(mediaUrlList[position])
    }

    override fun getItemCount(): Int {
        return mediaUrlList.size
    }

    inner class MediaHolder(private var view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        /**
         * Try to download image via Url and bind image view
         * @param mediaUrl complete http url
         */
        fun bindItem(mediaUrl: String){
            Glide.with(context)
                .load(mediaUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.color.colorPrimaryLight)
                .into(view.picture_gallery_item)
        }

        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

}