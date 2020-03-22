package com.lab.zicevents.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.lab.zicevents.R
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.model.api.geocoding.Address
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.data.model.local.DataResult
import com.lab.zicevents.utils.base.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SharedViewModel(private val userRepo: UserRepository) : ViewModel() {

    private val TAG = this::class.java.simpleName
    private var authUser: User? = null


    fun getAuthUser(): User? {
        return authUser
    }

    val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>().also {
            listenUserUpdates()
        }
    }

    fun getAuthUserRealTimeUpdates(): LiveData<User> {
        return user
    }

    private val updatedProfile = MutableLiveData<DataResult>()

    fun observeProfileChanges(): LiveData<DataResult> {
        return updatedProfile
    }

    private fun listenUserUpdates() {
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            GlobalScope.launch(Dispatchers.Main) {
                userRepo.listenUserUpdate(it).addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        authUser = null
                        Log.w(this@SharedViewModel.TAG, "Listen failed.", exception)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val fetchedUser = snapshot.toObject(User::class.java)
                        user.value = fetchedUser
                        authUser = fetchedUser

                    } else {
                        Log.w(this@SharedViewModel.TAG, "User with uid $it does not exist")
                        authUser = null
                    }
                }
            }
        }
    }

    /**
     * Update user profile Map<K,V> value
     * Pass result to LiveDate
     * @param docId is if of document who's contain data
     * @param map it's Map<String, Any?> corresponding data to fields and value
     */
    fun updateUserProfile(docId: String, map: Map<String, Any?>) {
        if (isProfileFieldsValid(map)) {
            GlobalScope.launch(Dispatchers.Main) {
                when (userRepo.updateUserProfile(docId, map)) {
                    is Result.Success ->
                        updatedProfile.value = DataResult(data = BaseRepository.SUCCESS_TASK)
                    is Result.Error -> updatedProfile.value =
                        DataResult(error = R.string.profile_update_fail)
                    is Result.Canceled -> updatedProfile.value =
                        DataResult(error = R.string.profile_update_cancel)
                }
            }
        } else {
            updatedProfile.value = DataResult(error = R.string.profile_update_fail)
        }
    }

    /**
     * Add user from subscriptions list
     * @param userIdToFollow id of user to follow
     */
    fun followUser(userIdToFollow: String){
        try {
            val authUser = getAuthUser()!!
            val userIdList = ArrayList<String>()
            getAuthUser()!!.subscriptions?.let { list ->
                userIdList.addAll(list)
            }

            if (!userIdList.contains(userIdToFollow)){
                userIdList.add(userIdToFollow)
                val map = HashMap<String, ArrayList<String>>()
                map[User.SUBSCRIPTIONS_FIELD] = userIdList
                updateUserProfile(authUser.userId, map)
            }
        } catch (e: NullPointerException){
            Log.e(TAG,"", e)
        }
    }

    /**
     * Remove user from subscriptions list
     * @param userIdToFollow id of user to unfollow
     */
    fun unfollowUser(userIdToFollow: String){
        try {
            val authUser = getAuthUser()!!
            val userIdList = ArrayList<String>()
            getAuthUser()!!.subscriptions?.let { list ->
                userIdList.addAll(list)
            }

            if (userIdList.contains(userIdToFollow)){
                userIdList.remove(userIdToFollow)
                val map = HashMap<String, ArrayList<String>>()
                map[User.SUBSCRIPTIONS_FIELD] = userIdList
                updateUserProfile(authUser.userId, map)
            }
        } catch (e: NullPointerException){
            Log.e(TAG,"", e)
        }
    }

    /**
     * Check if all value in Map<K,V> are correctly formatted
     * @return isValid Boolean
     */
    private fun isProfileFieldsValid(map: Map<String, Any?>): Boolean {
        var isValid = true
        map.keys.forEach {
            when (it) {
                User.PROFILE_IMAGE_FIELD,
                User.COVER_IMAGE_FIELD,
                User.DESCRIPTION_FIELD -> if (map[it] !is String?) {
                    Log.w(TAG, "'${User.DESCRIPTION_FIELD}' must be null or a String")
                    isValid = false
                    return@forEach
                }
                User.DISPLAY_NAME_FIELD -> if (map[it] !is String) {
                    Log.w(TAG, "'${User.DESCRIPTION_FIELD}' must be not null String")
                    isValid = false
                    return@forEach
                }
                User.MUSIC_STYLE_FIELD,
                User.SUBSCRIPTIONS_FIELD -> if (map[it] !is ArrayList<*>?) {
                    Log.w(TAG, "'${User.MUSIC_STYLE_FIELD}' must be an ArrayList of String or null")
                    isValid = false
                    return@forEach
                }
                User.GALLERY_FIELD -> if (map[it] !is StorageReference) {
                    Log.w(
                        TAG,
                        "'${User.DESCRIPTION_FIELD}' must be an instance of StorageReference"
                    )
                    isValid = false
                    return@forEach
                }
                User.ADDRESS_FIELD -> if (map[it] !is Address?) {
                    Log.w(TAG, "'${User.ADDRESS_FIELD}' must be an instance of Address or null")
                    isValid = false
                    return@forEach
                }
                else -> {
                    Log.w(TAG, "'${it}' is not valid field for user")
                    isValid = false
                    return@forEach
                }
            }
        }
        return isValid
    }
}