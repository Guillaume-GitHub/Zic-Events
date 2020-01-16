package com.lab.zicevents.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.lab.zicevents.R
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.database.publication.PublicationRepository
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.data.model.local.DataResult
import com.lab.zicevents.data.model.local.PublicationListResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat

class ProfileViewModel(private val userRepo: UserRepository,
                       private val publicationRepo: PublicationRepository
) : ViewModel() {

    private val TAG = this::class.java.simpleName
    val dateFormat: DateFormat = SimpleDateFormat.getDateInstance()

    private val userProfile = MutableLiveData<DataResult>()
    val profileData: LiveData<DataResult> = userProfile

    private val publicationList = MutableLiveData<PublicationListResult>()
    val userPublications: LiveData<PublicationListResult> = publicationList

    /**
     * *Coroutine*
     * Get User with uid from Firestore database
     * observe LoginViewModel.profileData to get result
     * @param uid String that corresponding to user uid
     */
    fun getFirestoreUser(uid: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = userRepo.getFirestoreUser(uid)
            userProfileDataChanged(result)
        }
    }

    /**
     * Get User publications with uid from Firestore database
     * @param userId String that corresponding to user uid
     */
    fun getUserPublications(userId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            when(val result = publicationRepo.getUserPublications(userId)){
                is Result.Success -> {
                    val publications: List<Publication> = result.data.toObjects(Publication::class.java)
                    publicationList.value = PublicationListResult(list = publications)
                }
                is Result.Error ->  publicationList.value = PublicationListResult(error = R.string.fetch_user_publication_error)
                is Result.Canceled ->  publicationList.value = PublicationListResult(error = R.string.fetch_user_publication_cancel)
            }
        }
    }


    /**
     * Send result of LoginViewModel.getFirestoreUser to LoginViewModel.profileUserSate Live data
     * Observe result with LoginViewModel.profileUserSate
     * @param result Result<DocumentSnapshot> value return by userRepo.getFirestoreUser(uid: String)
     */
    private fun userProfileDataChanged(result: Result<DocumentSnapshot>) = when(result) {
        is Result.Success -> {
            val user: User? = result.data.toObject(User::class.java)
            userProfile.value = DataResult(data = user)
        }
        is Result.Error -> {
            Log.w(TAG,"Error when trying to get user from firestore", result.exception)
            userProfile.value = DataResult(error = R.string.fetching_user_error)
        }
        is Result.Canceled ->  {
            Log.w(TAG,"Action canceled", result.exception)
            userProfile.value = DataResult(error = R.string.fetching_user_canceled)
        }
    }

}