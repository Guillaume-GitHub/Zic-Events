package com.lab.zicevents.ui.publication

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth

import com.lab.zicevents.R
import com.lab.zicevents.activity.SharedViewModel
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.utils.MarginItemDecoration
import com.lab.zicevents.utils.adapter.NetworkConnectivity
import com.lab.zicevents.utils.adapter.PublicationRecyclerAdapter
import com.lab.zicevents.utils.adapter.UserMediaRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_details_user.*
import java.text.SimpleDateFormat

class DetailsUserFragment : Fragment() {

    private val args: DetailsUserFragmentArgs by navArgs()
    private lateinit var publicationViewModel: PublicationViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var publicationRecycler: RecyclerView
    private lateinit var mediaAdapter: UserMediaRecyclerAdapter
    private lateinit var publicationAdapter: PublicationRecyclerAdapter
    private lateinit var mediaRecycler: RecyclerView
    private var publications = ArrayList<Publication>()
    private var medias = ArrayList<String>()
    private lateinit var userId: String
    private var user: User? = null
    private var isFollow: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.userId = args.userId
        retainInstance = true
        setHasOptionsMenu(true)
        initViewModel()
        initSharedViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!NetworkConnectivity.isOnline(context))
            Toast.makeText(context, getText(R.string.no_network_connectivity), Toast.LENGTH_SHORT).show()

        observeProfileResult()
        observeAuthUserProfileUpdates()
        getUserProfile()
        mediaRecyclerConfig()
        publicationRecyclerConfig()
        user_details_follow_btn.setOnClickListener {
            if (isFollow) sharedViewModel.unfollowUser(userId)
            else sharedViewModel.followUser(userId)
        }
    }

    /**
     * Init Publication ViewModel
     */
    private fun initViewModel() {
        publicationViewModel = ViewModelProviders
            .of(this, PublicationViewModelFactory())
            .get(PublicationViewModel::class.java)
    }

    private fun initSharedViewModel() {
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
    }

    /**
     * Configure publication recyclerView
     */
    private fun publicationRecyclerConfig() {
        publicationRecycler = user_details_publication_recyclerView
        publicationRecycler.layoutManager = LinearLayoutManager(context)
        publicationAdapter = PublicationRecyclerAdapter(context!!, publications)
        publicationRecycler.adapter = publicationAdapter
        publicationRecycler.addItemDecoration(MarginItemDecoration(10))
    }

    /**
     * Configure Media recyclerView
     */
    private fun mediaRecyclerConfig() {
        mediaRecycler = user_details_gallery_recyclerView
        mediaRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mediaAdapter = UserMediaRecyclerAdapter(context!!, medias)
        mediaRecycler.adapter = mediaAdapter
        mediaRecycler.addItemDecoration(MarginItemDecoration(10))
        LinearSnapHelper().attachToRecyclerView(mediaRecycler)
    }

    /**
     * Get user profile
     * get his profile or display error message
     */
    private fun getUserProfile() {
        publicationViewModel.getUserProfile(userId)
    }

    /**
     * Observe user fetching operation
     * get and pass result to fetch publications or display error message
     */
    private fun observeProfileResult() {
        publicationViewModel.profileResult.observe(viewLifecycleOwner, Observer {
            when {
                it.data is User -> {
                    user = it.data
                    updateUI(it.data)
                    getUserPublications()
                }
                it.error != null -> {
                    Toast.makeText(context, getString(it.error), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    /**
     * Get User publication from database if userId is not null
     * Init result Observer
     */
    private fun getUserPublications() {
        publicationViewModel.getUserPublications(userId)
        observePublicationResult()
    }

    /**
     * Observe get user publication result
     * Display publication in recycler or display message if error
     */
    private fun observePublicationResult() {
        publicationViewModel.userPublications.observe(viewLifecycleOwner, Observer {
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
                    Toast.makeText(
                        context,
                        getString(publicationsResult.error),
                        Toast.LENGTH_LONG
                    ).show()
            }
        })
    }

    /**
     * Update ui with new user userProfileResult
     */
    private fun updateUI(user: User) {
        Log.d(this::class.java.simpleName, "UPDATE UI")
        // Username view
        user_details_username.text = user.displayName
        // Pseudo view
        user_details_pseudo.text = user.pseudo
        // Registration date view
        user_details_registration_date.apply {
            if (user.createdDate != null) {
                val registrationDate =
                    "${getString(R.string.profile_registration_date_placeholder)} ${SimpleDateFormat.getDateInstance().format(
                        user.createdDate
                    )}"
                text = registrationDate
            } else visibility = View.GONE
        }
        // Description view
        user_details_description.apply {
            text = if (user.description != null) user.description
            else getText(R.string.profile_description_placeholder)
        }
        // Address view
        user_details_address.apply {
            visibility = View.GONE
            user.address?.let {
                text = it.formattedAddress
                visibility = View.VISIBLE
            }
        }
        // Followers view
        user_details_followers.apply {
            val size: Int? = user.subscriptions?.size
            text = if (size != null) "$size" else "0"
        }
        // Media Recycler View
        if (user.gallery != null) {
            medias.clear() // Clear old values
            medias.addAll(user.gallery!!) // pass values to medias list
            mediaAdapter.notifyDataSetChanged() // Update recycler view userProfileResult
        }
        // Profile Image
        user_details_image.apply {
            val url = user.profileImage
            if (url != null) loadImage(this, url)
            else setImageResource(android.R.color.transparent)
        }
        // Cover Image
        user_details_profile_cover_image.apply {
            val url = user.coverImage
            if (url != null) loadImage(this, url)
            else setImageResource(android.R.color.transparent)
        }
        // Set follow Button
        sharedViewModel.getAuthUser()?.let {
            val follow = publicationViewModel.isAlreadyFollow(userId, it)
            if (follow != null)
                setUserFollowButton(true)
            else
                user_details_follow_btn.visibility = View.GONE
        }
        // Set current user
        this.user = user
    }

    /**
     * Apply follow button style
     * @param isFollow boolean change background color
     */
    private fun setUserFollowButton(isFollow: Boolean) {
        this.isFollow = isFollow

        user_details_follow_btn.apply {
            visibility = View.VISIBLE
            val background = if (isFollow) R.color.colorAccent else android.R.color.transparent
            backgroundTintList = ContextCompat.getColorStateList(context, background)
        }
    }

    /**
     * Try to download image via Url and bind image view
     * @param url complete image http url
     * @param view imageView where to load image
     */
    private fun loadImage(view: ImageView, url: String) {
        Glide.with(context!!)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.color.colorPrimaryLight)
            .into(view)
    }

    /**
     * Load user publication media to image gallery
     * update recycler view
     * @param publications list of user publication
     */
    private fun addMediaToGallery(publications: ArrayList<Publication>) {
        medias.clear() // clean media list
        // add media from publication
        publications.forEach { item ->
            item.mediaUrl?.let {
                medias.add(it)
            }
        }
        // add media from gallery
        user?.let {
            val images = it.gallery
            if (!images.isNullOrEmpty())
                medias.addAll(images)
        }
        //update adapter
        mediaAdapter.notifyDataSetChanged()
    }

    /**
     * Observe auth user profile changes
     * Check if this user is already follow and change follow button state
     */
    private fun observeAuthUserProfileUpdates() {
        sharedViewModel.getAuthUserRealTimeUpdates().observe(viewLifecycleOwner, Observer {
            val subscriptions = it.subscriptions
            this.isFollow = !subscriptions.isNullOrEmpty() && subscriptions.contains(userId)
            setUserFollowButton(this.isFollow)
        })
    }
}
