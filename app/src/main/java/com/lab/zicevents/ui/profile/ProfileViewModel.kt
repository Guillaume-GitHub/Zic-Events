package com.lab.zicevents.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.lab.zicevents.R
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.database.UserRepository
import com.lab.zicevents.data.model.database.User
import com.lab.zicevents.data.model.local.ProfileUserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val TAG = this::class.java.simpleName

    private val userProfile = MutableLiveData<ProfileUserData>()
    val userProfileData: LiveData<ProfileUserData> = userProfile

    /**
     * *Coroutine*
     * Get User with uid from Firestore database
     * observe LoginViewModel.profileUserData to get result
     * @param uid String that corresponding to user uid
     */
    fun getFirestoreUser(uid: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = userRepository.getFirestoreUser(uid)
            userProfileDataChanged(result)
        }
    }

    /**
     * Send result of LoginViewModel.getFirestoreUser to LoginViewModel.profileUserSate Live data
     * Observe result with LoginViewModel.profileUserSate
     * @param result Result<DocumentSnapshot> value return by userRepository.getFirestoreUser(uid: String)
     */
    private fun userProfileDataChanged(result: Result<DocumentSnapshot>) = when(result) {
        is Result.Success -> {
            val user: User? = result.data.toObject(User::class.java)
            userProfile.value = ProfileUserData(firestoreUser = user)
        }
        is Result.Error -> {
            Log.w(TAG,"Error when trying to get user from firestore", result.exception)
            userProfile.value = ProfileUserData(error = R.string.fetching_user_error)
        }
        is Result.Canceled ->  {
            Log.w(TAG,"Action canceled", result.exception)
            userProfile.value = ProfileUserData(error = R.string.fetching_user_canceled)
        }
    }

}