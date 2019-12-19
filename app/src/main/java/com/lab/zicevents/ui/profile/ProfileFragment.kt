package com.lab.zicevents.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.User

import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initViewModel()
        getUserProfile(auth.currentUser!!.uid)

    }

    private fun initViewModel(){
       this.profileViewModel = ViewModelProviders.of(this, ProfileViewModelFactory()).get(ProfileViewModel::class.java)
    }

    private fun getUserProfile(uid: String){
        profileViewModel.getFirestoreUser(uid)
        observeUserProfileData()
    }

    private fun observeUserProfileData(){
        profileViewModel.userProfileData.observe(this, Observer {
            val userData = it
            when {
                userData.firestoreUser != null -> bindView(userData.firestoreUser)
                userData.error != null -> Toast.makeText(context,getString(userData.error),Toast.LENGTH_LONG).show() // TODO : Display Error profile Fragment
                else -> Log.w(this.javaClass.simpleName,getString(R.string.error_when_fetch_user))
            }
        })
    }
    private fun bindView(userData: User){
        fragment_profile_user_name_textView.text = userData.displayName
        fragment_profile_collapse_toolbar.title = userData.displayName
        fragment_profile_user_description_textView.text = userData.userId
    }

}