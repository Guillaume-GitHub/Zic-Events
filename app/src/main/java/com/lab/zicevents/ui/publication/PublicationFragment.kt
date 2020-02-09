package com.lab.zicevents.ui.publication

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lab.zicevents.MainActivity

import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.utils.OnActivityFabClickListener
import com.lab.zicevents.utils.OnPublicationClickListener
import com.lab.zicevents.utils.adapter.PublicationRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_publication.*

class PublicationFragment : Fragment(), OnActivityFabClickListener, OnPublicationClickListener {
    private lateinit var publicationViewModel: PublicationViewModel
    // RecyclerView
    private lateinit var publicationRecycler: RecyclerView
    private lateinit var publicationAdapter: PublicationRecyclerAdapter
    private var publications = ArrayList<Publication>()
    private var currentUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getAuthUser()
        (activity as? MainActivity)?.registerFabClickCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_publication, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        observeProfileResult()
        observePublicationsResult()
        observePublicationCreation()
        observeNewPublicationsResult()
        fragment_publication_progress.visibility = View.VISIBLE

        fragment_publication_swipeRefresh.setOnRefreshListener {
            getLastPublications()
        }
    }

    /**
     * Init Publication ViewModel
     */
    private fun initViewModel(){
        this.publicationViewModel = ViewModelProviders
            .of(this, PublicationViewModelFactory())
            .get(PublicationViewModel::class.java)
    }

    /**
     * Configure publication recyclerView
     */
    private fun configureRecyclerView(){
        publicationRecycler = fragment_publication_recyclerView
        publicationRecycler.layoutManager = LinearLayoutManager(context)
        publicationAdapter = PublicationRecyclerAdapter(context!!, publications, this)
        publicationRecycler.adapter = publicationAdapter
        publicationRecycler.addItemDecoration((DividerItemDecoration(context, DividerItemDecoration.VERTICAL)))
    }

    /**
     * Get current auth user
     * get his profile or display error message
     */
    private fun getAuthUser(){
        val currentUser = publicationViewModel.authUser
        if (currentUser != null)
            publicationViewModel.getUserProfile(currentUser.uid)
        else {
            Toast.makeText(context,getText(R.string.error_when_fetch_user), Toast.LENGTH_LONG).show()
            displayPlaceholder()
        }
    }

    /**
     * Observe user fetching operation
     * get and pass result to fetch publications or display error message
     */
    private fun observeProfileResult(){
        publicationViewModel.profileResult.observe(this, Observer {
            when {
                it.data is User -> {
                    currentUser = it.data
                    getPublications(it.data)
                }
                it.error != null -> {
                    Toast.makeText(context,getString(it.error), Toast.LENGTH_LONG).show()
                    displayPlaceholder()
                }
            }
        })
    }

    /**
     * Fetch publications to database
     * Get all publications of user 's subscriptions
     */
    private fun getPublications(user: User?) {
        if (user != null) {
            val subscriptions = ArrayList<String>()
            user.subscriptions?.let {
                subscriptions.addAll(it)
            }
            // Include current user id to subscriptions List to get his own publications
            subscriptions.add(user.userId)
            // Fetch publication(s)
            publicationViewModel.getSubscribedPublications(subscriptions)
        }
        else
            displayPlaceholder()
    }

    /**
     * Observe publications fetching operation
     * get and add result to recyclerView or display error message
     */
    private fun observePublicationsResult(){
        publicationViewModel.publicationList.observe(this, Observer {
            fragment_publication_progress.visibility = View.GONE
            when {
                it.data is ArrayList<*> -> {
                        val list = it.data.filterIsInstance<Publication>()
                        publications.clear()
                        publications.addAll(list)
                        publicationAdapter.notifyDataSetChanged()
                }
                it.error != null -> {
                    Toast.makeText(context,getString(it.error), Toast.LENGTH_LONG).show()
                    displayPlaceholder()
                }
            }
        })
    }

    /**
     * Fetch publications to database
     * Get all publications of user 's subscriptions
     */
    private fun getLastPublications() {
        val user = currentUser
        if (user != null && publications.isNotEmpty()){
            // set swipeRefreshLayout to refreshing
            fragment_publication_swipeRefresh.isRefreshing = true
            val subscriptions = ArrayList<String>()
            user.subscriptions?.let { subscriptions.addAll(it) }
            // Include current user id to subscriptions List to get his own publications
            subscriptions.add(user.userId)
            // Fetch publication(s)
            publicationViewModel.getLastSubscribedPublications(subscriptions, publications[0])
        }
        else
            fragment_publication_swipeRefresh.isRefreshing = false
    }

    /**
     * Observe new publications after fetching operation
     * get and add result to recyclerView or display error message
     */
    private fun observeNewPublicationsResult(){
        publicationViewModel.newPublicationList.observe(this, Observer {
            fragment_publication_swipeRefresh.isRefreshing = false // set refresh to false
            when {
                it.data is ArrayList<*> -> {
                    val list = it.data.filterIsInstance<Publication>()
                    if (!list.isNullOrEmpty()){
                        publications.addAll(0,list)
                        publicationAdapter.notifyItemInserted(0)
                    }
                }
                it.error != null -> {
                    Toast.makeText(context, getString(it.error), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    /**
     * Observe publication creation, hide progress dialog and dismiss
     */
    private fun observePublicationCreation(){
        publicationViewModel.publicationCreationSate.observe(this, Observer {
            when{
                it.data is Int -> getLastPublications()
                it.error != null ->
                    Toast.makeText(context, getText(it.error), Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * Show empty result fragment
     */
    private fun displayPlaceholder(){
        val action = PublicationFragmentDirections
            .actionBottomNavigationPublicationToEmptyPublicationPlaceholder()
        findNavController().navigate(action)
    }

    override fun onFabClick() {
        val ft = fragmentManager?.beginTransaction()
        ft?.let {
            AddPublicationFragmentDialog(publicationViewModel).show(ft, "AddPublicationDialog")
        }
    }

    /**
     * Display Publication details fragment
     */
    override fun onPublicationClick(publication: Publication, publicationOwner: User) {
        val action = PublicationFragmentDirections.fromPublicationToDetailsPublication(
            userId = publicationOwner.userId,
            username = publicationOwner.displayName,
            pseudo = publicationOwner.pseudo,
            profileImageUrl = publicationOwner.profileImage,
            message = publication.message,
            mediaUrl = publication.mediaUrl)

        findNavController().navigate(action)
    }
}
