package com.lab.zicevents.ui.profile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User

import kotlinx.android.synthetic.main.fragment_profile.*

import com.lab.zicevents.LoginActivity
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.utils.ImagePickerHelper
import com.lab.zicevents.utils.MarginItemDecoration
import com.lab.zicevents.utils.OnRequestPermissionsListener
import com.lab.zicevents.utils.PermissionHelper
import com.lab.zicevents.utils.adapter.PublicationRecyclerAdapter
import com.lab.zicevents.utils.adapter.UserMediaRecyclerAdapter
import com.lab.zicevents.utils.base.BaseRepository
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment: Fragment() ,View.OnClickListener {

    private lateinit var profileViewModel: ProfileViewModel
    private val auth = FirebaseAuth.getInstance()
    private var currentUser: User? = null
    private var oldImageProfile: Drawable? = null
    // RecyclerView
    private lateinit var publicationRecycler: RecyclerView
    private lateinit var mediaRecycler: RecyclerView
    // RecyclerView adapter
    private lateinit var publicationAdapter: PublicationRecyclerAdapter
    private lateinit var mediaAdapter: UserMediaRecyclerAdapter
    //RecyclerView userProfileResult
    private var publications = ArrayList<Publication>()
    private var medias = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.initViewModel()
        // Fetch user information
        observeProfileChange()
        profileViewModel.listenUserUpdate(auth.currentUser!!.uid)
        getUserPublications()
        // Set click listener on views
        fragment_profile_edit_info_btn.setOnClickListener(this)
        fragment_profile_change_photo_btn.setOnClickListener(this)
        // Set Toolbar menu + menu item click listener
        fragment_profile_toolbar.inflateMenu(R.menu.profile_menu)
        fragment_profile_toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        // Init RecyclerView
        publicationRecyclerConfig()
        mediaRecyclerConfig()
    }

    /**
     * Trigger item click
     */
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fragment_profile_edit_info_btn ->
                navigateToEditProfileFragment()
            R.id.fragment_profile_change_photo_btn ->
                pickImageFromGallery(ImagePickerHelper.PROFILE_IMG_RQ)
            else -> {}
        }
    }

    /**
     * Set actions on menu items
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.profile_menu_logout -> {
                auth.signOut() // log out
                startActivity(Intent(context,LoginActivity::class.java))
                activity?.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Configure profileViewModel
     */
    private fun initViewModel(){
       this.profileViewModel = ViewModelProviders.of(this, ProfileViewModelFactory()).get(ProfileViewModel::class.java)
    }

    private fun navigateToEditProfileFragment(){
      currentUser?.let {
          val action = ProfileFragmentDirections.fromProfileToProfileEdit(it.userId)
          findNavController().navigate(action)
        }
    }

    /**
     * Configure publication recyclerView
     */
    private fun publicationRecyclerConfig(){
        publicationRecycler = fragment_profile_user_events_recyclerView
        publicationRecycler.layoutManager = LinearLayoutManager(context)
        publicationAdapter = PublicationRecyclerAdapter(context!!, publications)
        publicationRecycler.adapter = publicationAdapter
        publicationRecycler.addItemDecoration(MarginItemDecoration(10))
    }

    /**
     * Configure Media recyclerView
    */
    private fun mediaRecyclerConfig(){
        mediaRecycler = fragment_profile_gallery_recyclerView
        mediaRecycler.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL,false)
        mediaAdapter = UserMediaRecyclerAdapter(context!!, medias)
        mediaRecycler.adapter = mediaAdapter
        mediaRecycler.addItemDecoration(MarginItemDecoration(10))
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
                    currentUser = it.data
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
     * Get User publication from database if userId is not null
     * Init result Observer
     */
    private fun getUserPublications() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            profileViewModel.getUserPublications(userId)
            observePublicationResult()
        }
    }

    /**
     * Observe get user publication result
     * Display publication in recycler or display message if error
     */
    private fun observePublicationResult(){
        profileViewModel.userPublications.observe(this, Observer {
            val publicationsResult = it
            when {
                !publicationsResult.list.isNullOrEmpty() -> {
                    val list = publicationsResult.list
                    publications.clear()
                    publications.addAll(list)
                    publicationAdapter.notifyDataSetChanged()
                }
                publicationsResult.error != null ->
                    Toast.makeText(context, getString(publicationsResult.error), Toast.LENGTH_LONG).show()

                else -> {}
            }
        })
    }

    /**
     * Upload image file to remote Firebase Storage
     * Init result Observer
     * @param drawable drawable to upload
     * @param
     */
    private fun uploadImageFile(drawable: Drawable){
        val imageRef = UUID.randomUUID().toString()
        profileViewModel.uploadImageFile(auth.currentUser!!.uid, drawable, imageRef)
        observeUploadImageResult()
    }

    /**
     * Observe Upload Result
     * if file was correctly uploaded, add his storageReference to user profile
     * else null or error, display message
     */
    private fun observeUploadImageResult(){
        profileViewModel.uploadImageResult.observe(this, Observer {
            when {
                it.data is Uri? -> {
                    if (it.data != null){ updateUserProfile(auth.currentUser?.uid!!,
                        mapOf(Pair(User.PROFILE_IMAGE_FIELD, it.data.toString())))
                    }else {
                        restoreOldProfileImage(oldImageProfile)
                        Toast.makeText(context, getString(R.string.store_image_task_error), Toast.LENGTH_LONG)
                            .show()
                    }
                }
                it.error != null -> {
                    restoreOldProfileImage(oldImageProfile)
                    Toast.makeText(context, getString(it.error), Toast.LENGTH_LONG)
                        .show()
                }
                else -> {
                    restoreOldProfileImage(oldImageProfile)
                    Log.w(this.javaClass.simpleName, "Unknown Upload image error")
                }
            }
        })
    }

    /**
     * Update user profile with new values
     * Init result Observer
     * @param docId is if of document who's contain data
     * @param map it's Map<String, Any?> corresponding data to fields and value
     */
    private fun updateUserProfile(docId: String, map: Map<String, Any?>){
        observeUpdateProfileResult()
        profileViewModel.updateUserProfile(docId, map)
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
                    if (it.data == BaseRepository.SUCCESS_TASK) fragment_profile_user_image.alpha = 1F
                it.error != null -> {
                    restoreOldProfileImage(oldImageProfile)
                    Toast.makeText(context,getString(it.error), Toast.LENGTH_LONG).show()
                }
                else -> {
                    restoreOldProfileImage(oldImageProfile)
                    Log.w(this.javaClass.simpleName, "Unknown Update Profile Error")
                }
            }
        })
    }

    /**
     * Update ui with new user userProfileResult
     */
    private fun updateUI(user: User) {
        // Set current user
        currentUser = user
        // Username view
        fragment_profile_username.text = user.displayName
        // Toolbar username view
        fragment_profile_collapse_toolbar.title = user.displayName
        // Pseudo view
        fragment_fragment_profile_pseudo.text = user.pseudo
        // Registration date view
        fragment_profile_registration_date.apply {
            if (user.createdDate != null) {
                val registrationDate =
                    "${getString(R.string.profile_registration_date_placeholder)} ${profileViewModel.dateFormat.format(user.createdDate)}"
                text = registrationDate
            } else visibility = View.GONE
        }
        // Description view
        fragment_profile_user_description.apply {
            text = if (user.description != null) user.description
            else getText(R.string.profile_description_placeholder)
        }
        // Address view
        fragment_profile_address.apply {
            visibility = View.GONE
            user.address?.let {
                text = it.formattedAddress
                visibility = View.VISIBLE
            }
        }
        // Followers view
        fragment_profile_followers.apply {
            visibility = View.GONE
        }
        // Media Recycler View
        if (user.gallery != null) {
            medias.clear() // Clear old values
            medias.addAll(user.gallery!!) // pass values to medias list
            mediaAdapter.notifyDataSetChanged() // Update recycler view userProfileResult
        }
        // Profile Image
        if (!user.profileImage.isNullOrBlank()) {
            Glide.with(this)
                .load(user.profileImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.colorPrimaryLight)
                .into(fragment_profile_user_image)
        }
    }

    /**
     * Set user profile image state before change.
     * default background if last state was null
     * or last drawable image
     * @param oldDrawable is the drawable in user image view before a change
     */
    private fun restoreOldProfileImage(oldDrawable: Drawable?) {
        fragment_profile_user_image.apply {
            if (oldDrawable != null) setImageDrawable(oldDrawable)
            else setImageResource(android.R.color.transparent)
            alpha = 1F
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
                object: OnRequestPermissionsListener{
                    override fun onRequestPermissions(isGranted: Boolean, grantResult: Map<String, Int>) {
                        if (isGranted)  // Start image picker gallery
                            ImagePickerHelper.pickImageFromGallery(this@ProfileFragment, requestCode)
                        else
                            Toast.makeText(context,getString(R.string.storage_permission_denied), Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK)
            when (requestCode){
                ImagePickerHelper.PROFILE_IMG_RQ -> {
                    // Get Picked image and upload it if not null
                    val imageUri = data?.data
                    if (imageUri != null){
                        oldImageProfile = fragment_profile_user_image.drawable
                        fragment_profile_user_image.setImageURI(imageUri)
                        fragment_profile_user_image.alpha = 0.2F
                        uploadImageFile(fragment_profile_user_image.drawable)
                    }
                }
                else -> {}
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        super.onDestroyView()
        profileViewModel.detachUserUpdateListener()
    }
}