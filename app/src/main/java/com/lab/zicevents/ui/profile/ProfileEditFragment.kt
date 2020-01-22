package com.lab.zicevents.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User

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
                it.data is User -> {
                    // TODO : Update UI with Data
                }
                it.error != null ->
                    Toast.makeText(context,getString(it.error), Toast.LENGTH_LONG).show() // TODO : Display Error profile Fragment
                else ->
                    Log.w(this.javaClass.simpleName, getString(R.string.error_when_fetch_user))
            }
        })
    }
}
