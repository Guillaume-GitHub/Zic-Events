package com.lab.zicevents.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lab.zicevents.R

class UserMediaRecyclerAdapter(var mediaUrlList: ArrayList<String>)
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

        fun bindItem(mediaUrl: String){
            // TODO : Load image in imageView
            /// view.picture_gallery_item.background // Load Image
        }

        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

}