package com.lab.zicevents.utils.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User
import kotlinx.android.synthetic.main.search_recycler_item.view.*

class SearchUserRecyclerAdapter(private val context: Context?, var users: ArrayList<User>): RecyclerView.Adapter<SearchUserRecyclerAdapter.UserHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        return UserHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.search_recycler_item, parent,false))
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bindItem(users[position])
    }

    inner class UserHolder(private var view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        fun bindItem(user: User){
            view.search_user_username.text = user.displayName
            view.search_user_pseudo.text = user.pseudo
            user.profileImage?.let {
                loadImage(it)
            }
            view.setOnClickListener(this)
        }

       private fun loadImage(mediaUrl: String){
            context?.let {
                Glide.with(it)
                    .load(mediaUrl)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.color.colorPrimaryLight)
                    .into(view.search_user_image)
            }
        }

        override fun onClick(v: View?) {
            //
        }
    }
}