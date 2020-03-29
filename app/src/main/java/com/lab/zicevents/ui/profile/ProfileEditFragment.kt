package com.lab.zicevents.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.data.model.local.UploadedImageResult
import com.lab.zicevents.utils.ImagePickerHelper
import com.lab.zicevents.utils.OnRequestPermissionsListener
import com.lab.zicevents.utils.PermissionHelper
import com.lab.zicevents.utils.adapter.NetworkConnectivity
import com.lab.zicevents.utils.base.BaseRepository
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import java.io.InputStream
import java.util.*

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
        if (!NetworkConnectivity.isOnline(context))
            Toast.makeText(context, getText(R.string.no_network_connectivity), Toast.LENGTH_SHORT).show()

        super.onViewCreated(view, savedInstanceState)
        listenUserUpdates()
        observeUpdateProfileResult()
        observeProfileChange()
        observeUploadImageResult()
        profile_edit_description.setOnClickListener(this)
        profile_edit_add_style.setOnClickListener(this)
        profile_edit_location.setOnClickListener(this)
        profile_edit_profile_image.setOnClickListener(this)
        profile_edit_cover_image.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.profile_edit_description -> showEditDescriptionDialog()
            R.id.profile_edit_add_style -> showEditStyleDialog()
            R.id.profile_edit_location -> showEditLocationDialog()
            R.id.profile_edit_profile_image ->
                pickImageFromGallery(ImagePickerHelper.PROFILE_IMG_RQ)
            R.id.profile_edit_cover_image ->
                pickImageFromGallery(ImagePickerHelper.COVER_IMG_RQ)
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
        profileViewModel.userProfileResult.observe(viewLifecycleOwner, Observer {
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
        profileViewModel.updateProfileResult.observe(viewLifecycleOwner, Observer {
            when {
                it.data is Int ->
                    if (it.data == BaseRepository.SUCCESS_TASK)
                        Toast.makeText(context, getString(R.string.profile_up_to_date), Toast.LENGTH_LONG).show()
                it.error != null ->
                    Toast.makeText(context,getString(it.error), Toast.LENGTH_LONG).show()
                else ->
                    Log.w(this.javaClass.simpleName, "Unknown Update Profile Error")
            }
        })
    }

    private fun updateUI(user: User){
        // Set Cover Image
        profile_edit_cover_image.apply {
            val url = user.coverImage
            if (url != null) loadImage(this, url)
            else setImageResource(android.R.color.transparent)
        }
        // Set Profile Image
        profile_edit_profile_image.apply {
            val url = user.profileImage
            if (url != null) loadImage(this, url)
            else setImageResource(android.R.color.transparent)
        }
        // Set description
        profile_edit_description.apply {
            val text =  if (user.description != null) user.description else null
            setText(text)
        }
        // Set user address
        profile_edit_location.apply {
            val text =  if (user.address?.formattedAddress != null) user.address?.formattedAddress else null
            setText(text)
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

    /**
     * Create and Show LocationFragmentDialog as dialog
     */
    private fun showEditLocationDialog(){
        user?.let { user ->
            val ft = fragmentManager?.beginTransaction()
            ft?.let {
                LocationFragmentDialog(
                    userId = user.userId,
                    viewModel = profileViewModel,
                    address = user.address
                ).show(ft, "LocationFragmentDialog")
            }
        }
    }

    /**
     * Update user profile with new values
     * @param docId is if of document who's contain data
     * @param map it's Map<String, Any?> corresponding data to fields and value
     */
    private fun updateUserProfile(docId: String, map: Map<String, Any?>){
        profileViewModel.updateUserProfile(docId, map)
        // set default alpha
        when {
            map.containsKey(User.COVER_IMAGE_FIELD) -> profile_edit_cover_image.alpha = 1F
            map.containsKey(User.PROFILE_IMAGE_FIELD) -> profile_edit_profile_image.alpha = 1F
        }
    }


    /**
     * Check permissions, if is granted start Image Gallery Picker
     * else Ask these permissions to user
     * @param requestCode identifier of request
     */
    private fun pickImageFromGallery(requestCode: Int){
        val permsResult = PermissionHelper().checkPermissions(context!!, PermissionHelper.STORAGE_PERMISSIONS)
        // Ask permission to user if permission denied
        if (permsResult.isNullOrEmpty()){
            ImagePickerHelper.pickImageFromGallery(this, requestCode) // Start image picker gallery
        } else { // ASk permissions to user
            val activity = activity
            PermissionHelper().askRequestPermissions(activity,
                PermissionHelper.STORAGE_PERMISSIONS,
                object: OnRequestPermissionsListener {
                    override fun onRequestPermissions(isGranted: Boolean, grantResult: Map<String, Int>) {
                        if (isGranted)  // Start image picker gallery
                            ImagePickerHelper.pickImageFromGallery(this@ProfileEditFragment, requestCode)
                        else
                            Toast.makeText(context,getString(R.string.storage_permission_denied), Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    /**
     * Upload image file to remote Firebase Storage
     * Init result Observer
     * @param drawable drawable to upload
     * @param requestId it's an id to retrieve the request when observe result
     */
    private fun uploadImageFile(drawable: Drawable, requestId: Int){
        if (NetworkConnectivity.isOnline(context)) {
            val fileName = UUID.randomUUID().toString()
            profileViewModel.uploadImageFile(userId, drawable, fileName, requestId)
            profile_edit_cover_image.alpha = 0.2F // make it more transparent
        }
        else
            Toast.makeText(
                context,
                getText(R.string.no_network_connectivity),
                Toast.LENGTH_SHORT
            ).show()
    }

    /**
     * Observe Upload Result
     * if file was correctly uploaded, add his url to user profile
     * else null or error, display message
     */
    private fun observeUploadImageResult(){
        profileViewModel.uploadImageResult.observe(viewLifecycleOwner, Observer {
            if (it.error != null || it.imageUri == null) {
                // Display error
                Toast.makeText(context, getString(R.string.store_image_task_error),
                    Toast.LENGTH_LONG).show()
                // Restore old image view state
                when (it.requestId) {
                    ImagePickerHelper.COVER_IMG_RQ -> profile_edit_cover_image.alpha = 1F//restoreOldImage(oldCoverImg,
                       // profile_edit_cover_image)
                    ImagePickerHelper.PROFILE_IMG_RQ -> profile_edit_profile_image.alpha = 1F//restoreOldImage(oldProfileImg,
                        //profile_edit_profile_image)
                    else -> {}
                }
            } else {
                // Upload user profile this new image url
                when (it.requestId) {
                    ImagePickerHelper.COVER_IMG_RQ ->
                        updateUserProfile(userId, mapOf(Pair(User.COVER_IMAGE_FIELD,
                            it.imageUri.toString())))

                    ImagePickerHelper.PROFILE_IMG_RQ ->
                        updateUserProfile(userId, mapOf(Pair(User.PROFILE_IMAGE_FIELD,
                            it.imageUri.toString()))
                        )
                    else -> {}
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ImagePickerHelper.PROFILE_IMG_RQ -> {
                    // Get Picked image and upload it if not null
                    val imageUri = data?.data
                    imageUri?.let {
                        // show and upload profile image from his uri
                        ImagePickerHelper.getDrawableFromUri(context, it)?.let {drawable ->
                            uploadImageFile(drawable, requestCode)
                        }
                    }
                }
                ImagePickerHelper.COVER_IMG_RQ -> {
                    // Get Picked image and upload it if not null
                    val imageUri = data?.data
                    imageUri?.let { it ->
                        ImagePickerHelper.getDrawableFromUri(context, it)?.let {drawable ->
                            uploadImageFile(drawable, requestCode)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
