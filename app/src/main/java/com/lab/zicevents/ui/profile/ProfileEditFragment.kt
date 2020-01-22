package com.lab.zicevents.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.chip.Chip
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.ui.login.LoginViewModel
import kotlinx.android.synthetic.main.fragment_profile_edit.*

class ProfileEditFragment : Fragment() {
    private val args: ProfileEditFragmentArgs by navArgs()
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userRef: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userRef = args.userRef
        initViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserData()
    }

    /**
     * Configure profileViewModel
     */
    private fun initViewModel(){
        this.profileViewModel = ViewModelProviders.of(this, ProfileViewModelFactory()).get(ProfileViewModel::class.java)
    }

    /**
     * Fetch user data and init result observer
     */
    private fun fetchUserData() {
        profileViewModel.getUserByDocReference(userRef)
        observeFetchedProfileData()
    }

    /**
     * Observe result of fetching user data request
     */
    private fun observeFetchedProfileData() {
        profileViewModel.userProfileResult.observe(this, Observer {
            when {
                it.data is User ->
                   updateUI(it.data)
                it.error != null ->
                    // TODO : Display Error profile Fragment
                    Toast.makeText(context, getString(it.error), Toast.LENGTH_LONG).show()
                else ->
                    Log.w(this.javaClass.simpleName, getString(R.string.error_when_fetch_user))
            }
        })
    }

    private fun updateUI(user: User){
        // Set Cover Image
        user.coverImage?.let {
            loadImage(profile_edit_cover_image, it)
        }
        // Set Profile Image
        user.profileImage?.let {
            loadImage(profile_edit_profile_image, it)
        }
        // Set description
        user.description?.let {
            profile_edit_description.setText(it)
        }
        // Set Music Styles
        user.musicStyle?.let {
            it.forEach { text ->
                profile_edit_style_chipGroup.addView(
                    profileViewModel.getFormattedChip(context!!, text)
                )
            }
        }
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
