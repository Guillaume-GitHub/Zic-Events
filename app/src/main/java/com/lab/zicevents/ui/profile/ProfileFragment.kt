package com.lab.zicevents.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User

import kotlinx.android.synthetic.main.fragment_profile.*

import com.lab.zicevents.activity.LoginActivity
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.utils.ImagePickerHelper
import com.lab.zicevents.utils.MarginItemDecoration
import com.lab.zicevents.utils.OnRequestPermissionsListener
import com.lab.zicevents.utils.PermissionHelper
import com.lab.zicevents.utils.adapter.NetworkConnectivity
import com.lab.zicevents.utils.adapter.PublicationRecyclerAdapter
import com.lab.zicevents.utils.adapter.UserMediaRecyclerAdapter
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment: Fragment() ,View.OnClickListener {

    private lateinit var profileViewModel: ProfileViewModel
    private val auth = FirebaseAuth.getInstance()
    private var currentUser: User? = null
    // RecyclerView
    private lateinit var publicationRecycler: RecyclerView
    private lateinit var mediaRecycler: RecyclerView
    // RecyclerView adapter
    private lateinit var publicationAdapter: PublicationRecyclerAdapter
    private lateinit var mediaAdapter: UserMediaRecyclerAdapter
    //RecyclerView userProfileResult
    private var publications = ArrayList<Publication>()
    private var medias = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!NetworkConnectivity.isOnline(context))
            Toast.makeText(context, getText(R.string.no_network_connectivity), Toast.LENGTH_SHORT).show()

        this.initViewModel()
        // Fetch user information
        observeProfileChange()
        observeUploadImageResult()
        profileViewModel.listenUserUpdate(auth.currentUser!!.uid)
        getUserPublications()
        // Set click listener on views
        fragment_profile_edit_info_btn.setOnClickListener(this)
        fragment_profile_change_photo_btn.setOnClickListener(this)
        fragment_profile_cover_image.setOnClickListener(this)
        // Init RecyclerView
        publicationRecyclerConfig()
        mediaRecyclerConfig()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
            R.id.fragment_profile_cover_image ->
                pickImageFromGallery(ImagePickerHelper.COVER_IMG_RQ)
            else -> {}
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile_menu_logout -> {
                auth.signOut() // log out
                startActivity(Intent(context, LoginActivity::class.java))
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
        mediaRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        mediaAdapter = UserMediaRecyclerAdapter(context!!, medias)
        mediaRecycler.adapter = mediaAdapter
        mediaRecycler.addItemDecoration(MarginItemDecoration(10))
        LinearSnapHelper().attachToRecyclerView(mediaRecycler)
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
                    if (it.data != currentUser) {
                        currentUser = it.data
                        updateUI(it.data)
                    }
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
        profileViewModel.userPublications.observe(viewLifecycleOwner, Observer {
            val publicationsResult = it
            when {
                !publicationsResult.list.isNullOrEmpty() -> {
                    val list = publicationsResult.list
                    publications.clear()
                    publications.addAll(list)
                    publicationAdapter.notifyDataSetChanged()
                    addMediaToGallery(publications)
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
    private fun uploadImageFile(drawable: Drawable, requestId: Int){
        if (NetworkConnectivity.isOnline(context)) {
            val imageRef = UUID.randomUUID().toString()
            profileViewModel.uploadImageFile(auth.currentUser!!.uid, drawable, imageRef,requestId)
            fragment_profile_user_image.alpha = 0.2F // make it more transparent
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
     * if file was correctly uploaded, add his storageReference to user profile
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
                    ImagePickerHelper.COVER_IMG_RQ -> fragment_profile_cover_image.alpha = 1F
                    ImagePickerHelper.PROFILE_IMG_RQ -> fragment_profile_user_image.alpha = 1F
                    else -> {}
                }
            } else {
                // Upload user profile this new image url
                when (it.requestId) {
                    ImagePickerHelper.COVER_IMG_RQ ->
                        updateUserProfile(auth.currentUser!!.uid, mapOf(Pair(User.COVER_IMAGE_FIELD,
                            it.imageUri.toString())))

                    ImagePickerHelper.PROFILE_IMG_RQ ->
                        updateUserProfile(auth.currentUser!!.uid, mapOf(Pair(User.PROFILE_IMAGE_FIELD,
                            it.imageUri.toString()))
                        )
                    else -> {}
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
        profileViewModel.updateUserProfile(docId, map)
        // set default alpha
        when {
            map.containsKey(User.COVER_IMAGE_FIELD) -> fragment_profile_cover_image.alpha = 1F
            map.containsKey(User.PROFILE_IMAGE_FIELD) -> fragment_profile_user_image.alpha = 1F
        }
    }

    /**
     * Update ui with new user userProfileResult
     */
    private fun updateUI(user: User) {
        Log.d(this::class.java.simpleName, "UPDATE UI")
        // Username view
        fragment_profile_username.text = user.displayName
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
            val size: Int? = user.subscriptions?.size
            text = if (size != null) "$size" else "0"
        }
        // Profile Image
        fragment_profile_user_image.apply {
            val url = user.profileImage
            if (url != null) loadImage(this, url)
            else setImageResource(android.R.color.transparent)
        }
        // Cover Image
        fragment_profile_cover_image.apply {
            val url = user.coverImage
            if (url != null) loadImage(this, url)
            else setImageResource(android.R.color.transparent)
        }
        // Set current user
        currentUser = user
    }

    /**
     * Load user publication media to image gallery
     * update recycler view
     * @param publications list of user publication
     */
    private fun addMediaToGallery(publications: ArrayList<Publication>){
        medias.clear() // clean media list
        // add media from publication
        publications.forEach { item ->
            item.mediaUrl?.let{
                medias.add(it)
            }
        }
        // add media from gallery
        currentUser?.let {
            val images = it.gallery
            if (!images.isNullOrEmpty())
                medias.addAll(images)
        }
        //update adapter
        mediaAdapter.notifyDataSetChanged()
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
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileViewModel.detachUserUpdateListener()
    }
}