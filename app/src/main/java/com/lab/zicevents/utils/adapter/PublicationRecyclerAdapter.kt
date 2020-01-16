package com.lab.zicevents.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.publication.Publication
import kotlinx.android.synthetic.main.publication_recycler_item.view.*

class PublicationRecyclerAdapter(var publications: ArrayList<Publication>): RecyclerView.Adapter<PublicationRecyclerAdapter.PublicationHolder>() {

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

        fun bindView(publication: Publication){
            view.publication_dateTime.text = publication.createdDate.toString()
            view.publication_message.text = publication.message

            //TODO : Bind all views
        /*
            view.publication_username.text
            view.publication_user_image.background
            view.publication_image.apply {
                if (publication.mediaUrl != null)  // Load image
                else visibility = View.GONE
            }
         */
        }

        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
