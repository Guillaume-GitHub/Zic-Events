package com.lab.zicevents.utils.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.data.model.database.user.User
import kotlinx.android.synthetic.main.picture_gallery_recycler_item.view.*
import kotlinx.android.synthetic.main.publication_recycler_item.view.*
import java.text.SimpleDateFormat

class PublicationRecyclerAdapter(val context: Context, var publications: ArrayList<Publication>): RecyclerView.Adapter<PublicationRecyclerAdapter.PublicationHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationHolder {
        return PublicationHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.publication_recycler_item, parent, false))
    }

    override fun getItemCount(): Int {
        return publications.size
    }

    override fun onBindViewHolder(holder: PublicationHolder, position: Int) {
        holder.bindView(publications[position])
    }


    inner class PublicationHolder(private var view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        /**
         * Try to get publication user info
         * if successful update publication view
         */
        fun bindView(publication: Publication) {
            publication.user?.get()?.addOnCompleteListener { task ->
                when {
                    task.isSuccessful ->
                        updateView(task.result?.toObject(User::class.java), publication)
                    task.exception != null -> Log.w(
                        this::class.java.simpleName,
                        "Get user ref ",
                        task.exception
                    )
                }
            }
        }

        /**
         * Bind all publication item view
         */
        private fun updateView(user: User?, publication: Publication) {
            if (user != null) {
                publication.mediaUrl.apply {
                    if (!this.isNullOrBlank())
                        loadImage(view.publication_image, publication.mediaUrl!!)
                    else view.publication_image.visibility = View.GONE
                }

                user.profileImage?.let {
                    loadImage(view.publication_user_image, it)
                }

                view.publication_username.text = user.displayName
                view.publication_dateTime.text = SimpleDateFormat.getInstance().format(publication.createdDate!!)
                view.publication_message.text = publication.message
            }
        }

        /**
         * Try to download image via Url and bind image view
         * @param url complete http url
         * @param view imageView where to load image
         */
        private fun loadImage(view: ImageView, url: String){
                Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.color.colorPrimaryLight)
                    .into(view)
        }

        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
