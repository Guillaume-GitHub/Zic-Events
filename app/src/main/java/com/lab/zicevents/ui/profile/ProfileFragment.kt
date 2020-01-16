package com.lab.zicevents.ui.profile

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User

import kotlinx.android.synthetic.main.fragment_profile.*

import com.lab.zicevents.LoginActivity
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.utils.MarginItemDecoration
import com.lab.zicevents.utils.adapter.PublicationRecyclerAdapter
import com.lab.zicevents.utils.adapter.UserMediaRecyclerAdapter


class ProfileFragment : Fragment() ,View.OnClickListener {

    private lateinit var profileViewModel: ProfileViewModel
    private val auth = FirebaseAuth.getInstance()
    private var currentUser: User? = null
    // RecyclerView
    private lateinit var publicationRecycler: RecyclerView
    private lateinit var mediaRecycler: RecyclerView
    // RecyclerView adapter
    private lateinit var publicationAdapter: PublicationRecyclerAdapter
    private lateinit var mediaAdapter: UserMediaRecyclerAdapter
    //RecyclerView data
    private var publications = ArrayList<Publication>()
    private var medias = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.initViewModel()
        // Fetch user information
        getUserProfile(auth.currentUser!!.uid)
        // Set click listener on views
        fragment_profile_edit_info_btn.setOnClickListener(this)
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
                findNavController().navigate(R.id.from_profile_to_profile_edit)
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

    /**
     * Configure publication recyclerView
     */
    private fun publicationRecyclerConfig(){
        publicationRecycler = fragment_profile_user_events_recyclerView
        publicationRecycler.layoutManager = LinearLayoutManager(context)
        publicationAdapter = PublicationRecyclerAdapter(publications)
        publicationRecycler.adapter = publicationAdapter
        publicationRecycler.addItemDecoration(MarginItemDecoration(10))
    }

    /**
     * Configure Media recyclerView
    */
    private fun mediaRecyclerConfig(){
        mediaRecycler = fragment_profile_gallery_recyclerView
        mediaRecycler.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL,false)
        mediaAdapter = UserMediaRecyclerAdapter(medias)
        mediaRecycler.adapter = mediaAdapter
        mediaRecycler.addItemDecoration(MarginItemDecoration(10))
    }

    /**
     * Fetch user profile from Firesstore
     * and init Observer
     */
    private fun getUserProfile(uid: String){
        profileViewModel.getFirestoreUser(uid)
        userProfileFetchingResult()
    }

    /**
     * Observe User profile result fectched from Firestore
     * Pass new value in updateIU method to display user profile
     * if null or error display error message
     */
    private fun userProfileFetchingResult(){
        profileViewModel.profileData.observe(this, Observer {
            val dataResult = it
            when (dataResult.data){
                // case Result is instance of User?
                is User? -> {
                    when {
                        dataResult.data != null -> {
                            currentUser = dataResult.data
                            updateUI(dataResult.data)
                            getUserPublications()
                        }
                        dataResult.error != null ->
                            Toast.makeText(context,getString(dataResult.error), Toast.LENGTH_LONG).show() // TODO : Display Error profile Fragment
                        else ->
                            Log.w(this.javaClass.simpleName, getString(R.string.error_when_fetch_user))
                    }
                }
            }
        })
    }

    /**
     * Get User publication from database if userId is not null
     * Init result Observer
     */
    private fun getUserPublications() {
        val userId = currentUser?.userId
        if (userId != null) {
            profileViewModel.getUserPublications(userId)
            publicationFetchingResult()
        }
    }

    /**
     * Observe get user publication result
     * Display publication in recycler or display message if error
     */
    private fun publicationFetchingResult(){
        profileViewModel.userPublications.observe(this, Observer {
            val publicationsResult = it

            when {
                publicationsResult.list != null
                        && publicationsResult.list.isNotEmpty() -> {
                    val list = publicationsResult.list
                    Log.d("LIST PUBLICATION = ", list.toString())
                    publications.addAll(list)
                    publicationAdapter.notifyDataSetChanged()
                }
                publicationsResult.error != null ->
                    Toast.makeText(context,getString(publicationsResult.error), Toast.LENGTH_LONG).show()
                else ->
                    Log.w(this.javaClass.simpleName, getString(R.string.fetch_user_publication_error))
            }
        })
    }

    /**
     * Update ui with new user data
     */
    private fun updateUI(user: User){
        // Username view
        fragment_profile_username.text = user.displayName
        // Toolbar username view
        fragment_profile_collapse_toolbar.title = user.displayName
        // Pseudo view
        fragment_fragment_profile_pseudo.text = user.pseudo
        // Registration date view
        fragment_profile_registration_date.apply {
            if (user.createdDate != null) {
                val registrationDate = "${getString(R.string.profile_registration_date_placeholder)} ${profileViewModel.dateFormat.format(user.createdDate)}"
                text = registrationDate
            }
            else visibility = View.GONE
        }
        // Description view
        fragment_profile_user_description.apply {
            text = if (user.description != null) user.description
            else getText(R.string.profile_description_placeholder)
        }
        // Address view
        fragment_profile_address.apply {
            visibility = View.GONE
        }
        // Followers view
        fragment_profile_followers.apply {
            visibility = View.GONE
        }
        // Media Recycler View
        if (user.gallery != null) {
            val userMedia = user.gallery!!
            medias.addAll(userMedia) // pass values to medias list
            mediaAdapter.notifyDataSetChanged() // Update recycler view data
        }
    }
}