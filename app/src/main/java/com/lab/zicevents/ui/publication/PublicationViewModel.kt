package com.lab.zicevents.ui.publication

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.ListResult
import com.lab.zicevents.R
import com.lab.zicevents.data.database.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.database.publication.PublicationRepository
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.data.model.local.DataResult

class PublicationViewModel(private val publicationRepo: PublicationRepository,
                           private val userRepo: UserRepository): ViewModel() {

    val authUser = FirebaseAuth.getInstance().currentUser

    private val publications = MutableLiveData<DataResult>()
    val publicationList: LiveData<DataResult> = publications

    private val profile = MutableLiveData<DataResult>()
    val profileResult: LiveData<DataResult> = profile

    /**
     * Fetch publication from database async
     * Return result as LiveData
     */
    fun getSubscribedPublications(subscriptionList: List<String>) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = publicationRepo.getSubscribedPublications(subscriptionList)) {
                is Result.Success -> {
                    val list : ArrayList<Publication> = ArrayList()
                    list.addAll(result.data.toObjects(Publication::class.java))
                    publications.value = DataResult(data = list)
                }
                is Result.Error -> {
                    Log.e(TAG, "Error when trying to get publications", result.exception)
                    publications.value = DataResult(error = R.string.fetching_publication_error)
                }
                is Result.Canceled -> {
                    Log.e(TAG, "Action canceled", result.exception)
                    publications.value = DataResult(error = R.string.fetching_user_canceled)
                }
            }
        }
    }

    /**
     * Get User with uid from Firestore database
     * set live data var
     * @param userId String that corresponding to user uid
     */
    fun getUserProfile(userId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = userRepo.getFirestoreUser(userId)) {
                is Result.Success -> {
                    profile.value = DataResult(data = result.data.toObject(User::class.java))
                }
                is Result.Error -> {
                    Log.w(TAG, "Error when trying to get user info from firestore", result.exception)
                    profile.value = DataResult(error = R.string.fetching_user_error)
                }
                is Result.Canceled -> {
                    Log.w(TAG, "Action canceled", result.exception)
                    profile.value = DataResult(error = R.string.fetching_user_canceled)
                }
            }
        }
    }
}
