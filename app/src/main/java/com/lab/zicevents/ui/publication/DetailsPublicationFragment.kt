package com.lab.zicevents.ui.publication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_details_publication.*

class DetailsPublicationFragment : Fragment() {

    private val args: DetailsPublicationFragmentArgs by navArgs()
    private lateinit var userId: String
    private lateinit var username: String
    private lateinit var pseudo: String
    private lateinit var message: String
    private var profileImageUrl: String? = null
    private var mediaUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_publication, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    /**
     * Get args passed to fragment
     */
    private fun getArgs(){
        userId = args.userId
        username = args.username
        pseudo = args.pseudo
        message = args.message
        profileImageUrl = args.profileImageUrl
        mediaUrl = args.mediaUrl
    }


    /**
     * Update views with agrs
     */
    private fun updateUI(){
        profileImageUrl?.let {
            loadImage(details_publication_user_image, it)
        }
        mediaUrl?.let {
            loadImage(details_publication_image, it)
            details_publication_image.visibility = View.VISIBLE
        }
        details_publication_username.text = username
        details_publication_pseudo.text = pseudo
        details_publication_message.text = message
    }

    /**
     * Try to download image via Url and bind image view
     * @param url complete image http url
     * @param view imageView where to load image
     */
    private fun loadImage(view: ImageView, url: String){
        Glide.with(context!!)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.color.colorPrimaryLight)
            .into(view)
    }
}
