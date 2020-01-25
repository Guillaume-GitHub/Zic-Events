package com.lab.zicevents.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.utils.base.BaseRepository
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_style_dialog.*

class ProfileEditFragment : Fragment(), View.OnClickListener {
    private val args: ProfileEditFragmentArgs by navArgs()
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userId: String
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = args.userId
        initViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenUserUpdates()
        observeUpdateProfileResult()
        observeProfileChange()
        profile_edit_description.setOnClickListener(this)
        profile_edit_add_style.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.profile_edit_description -> showEditDescriptionDialog()
            R.id.profile_edit_add_style -> showEditStyleDialog()
            else -> {}
        }
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
    private fun listenUserUpdates() {
        Log.d(this::class.java.simpleName, "User Update")
        profileViewModel.listenUserUpdate(userId)
    }

    /**
     * Observe User profile result fetched from Firestore
     * Pass new value in updateIU method to display user profile
     * if null or error display error message
     */
    private fun observeProfileChange(){
        profileViewModel.userProfileResult.observe(this, Observer {
            when {
                it.data is User -> {
                    Log.d(this::class.java.simpleName, it.data.toString())
                    user = it.data
                    updateUI(it.data)
                }
                it.error != null ->
                    Toast.makeText(context,getString(it.error), Toast.LENGTH_LONG).show() // TODO : Display Error profile Fragment
                else ->
                    Log.w(this.javaClass.simpleName, getString(R.string.error_when_fetch_user))
            }
        })
    }

    /**
     * Observe update profile Result
     * if profile was correctly updated, show new image in profile imageView
     * else null or error, display message
     */
    private fun observeUpdateProfileResult(){
        profileViewModel.updateProfileResult.observe(this, Observer {
            when {
                it.data is Int ->
                    if (it.data == BaseRepository.SUCCESS_TASK)
                        Toast.makeText(context, " Profile mis à jour", Toast.LENGTH_LONG).show() //TODO : replace String
                it.error != null ->
                    Toast.makeText(context,getString(it.error), Toast.LENGTH_LONG).show()
                else ->
                    Log.w(this.javaClass.simpleName, "Unknown Update Profile Error")
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
        user.musicStyle.apply {
            // Clean chip group
            if (profile_edit_style_chipGroup.isNotEmpty())
                profile_edit_style_chipGroup.removeAllViews()
            // Add Chip in Chip group
            if (!this.isNullOrEmpty()){
                forEach { text ->
                    profile_edit_style_chipGroup.apply {
                        addView(profileViewModel.getFormattedChip(context!!, text))
                    }
                }
            }
        }
        this.user = user
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

    /**
     * Create and Show DescriptionFragmentDialog as dialog
     */
    private fun showEditDescriptionDialog(){
        user?.let {user ->
            val ft = fragmentManager?.beginTransaction()
            ft?.let {
                DescriptionFragmentDialog(
                    userId =  user.userId,
                    description = user.description,
                    viewModel = profileViewModel
                ).show(ft, "DescriptionFragmentDialog")
            }
        }
    }

    /**
     * Create and Show DescriptionFragmentDialog as dialog
     */
    private fun showEditStyleDialog(){
        user?.let {user ->
            val ft = fragmentManager?.beginTransaction()
            ft?.let {
                StyleFragmentDialog(
                    userId = userId,
                    viewModel = profileViewModel,
                    styleList = user.musicStyle
                ).show(ft, "StyleFragmentDialog")
            }
        }
    }
}
