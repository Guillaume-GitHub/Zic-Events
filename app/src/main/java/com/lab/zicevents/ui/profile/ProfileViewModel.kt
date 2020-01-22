package com.lab.zicevents.ui.profile

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.StorageReference
import com.lab.zicevents.R
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.database.publication.PublicationRepository
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.data.model.local.DataResult
import com.lab.zicevents.data.model.local.PublicationListResult
import com.lab.zicevents.data.storage.StorageRepository
import com.lab.zicevents.utils.base.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat

class ProfileViewModel(private val userRepo: UserRepository,
                       private val publicationRepo: PublicationRepository,
                       private val storageRepo: StorageRepository) : ViewModel() {

    private val TAG = this::class.java.simpleName
    private var userUpdateListener: ListenerRegistration? = null
    val dateFormat: DateFormat = SimpleDateFormat.getDateInstance()

    private val profileDataResult = MutableLiveData<DataResult>()
    val userProfileResult: LiveData<DataResult> = profileDataResult

    private val uploadedImage = MutableLiveData<DataResult>()
    val uploadImageResult: LiveData<DataResult> = uploadedImage

    private val publicationList = MutableLiveData<PublicationListResult>()
    val userPublications: LiveData<PublicationListResult> = publicationList

    private val updatedProfile = MutableLiveData<DataResult>()
    val updateProfileResult: LiveData<DataResult> = updatedProfile

    /**
     * *Coroutine*
     * Get User with uid from Firestore database
     * observe LoginViewModel.userProfileResult to get result
     * @param uid String that corresponding to user uid
     */
    fun getFirestoreUser(uid: String) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = userRepo.getUserByReference(uid)) {
                is Result.Success -> {
                    profileDataResult.value = DataResult(data = result.data.toObject(User::class.java))
                }
                is Result.Error -> {
                    Log.w(TAG, "Error when trying to get user info from firestore", result.exception)
                    profileDataResult.value = DataResult(error = R.string.fetching_user_error)
                }
                is Result.Canceled -> {
                    Log.w(TAG, "Action canceled", result.exception)
                    profileDataResult.value = DataResult(error = R.string.fetching_user_canceled)
                }
            }
        }
    }

    /**
     * Get User with is document reference in database
     * @param documentRef String that corresponding to user document path in database
     */
    fun getUserByDocReference(documentRef: String) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = userRepo.getUserByReference(documentRef)) {
                is Result.Success -> {
                    profileDataResult.value = DataResult(data = result.data.toObject(User::class.java))
                }
                is Result.Error -> {
                    Log.w(TAG, "Error when trying to get user info from firestore", result.exception)
                    profileDataResult.value = DataResult(error = R.string.fetching_user_error)
                }
                is Result.Canceled -> {
                    Log.w(TAG, "Action canceled", result.exception)
                    profileDataResult.value = DataResult(error = R.string.fetching_user_canceled)
                }
            }
        }
    }

    /**
     *  Set Realtime update listener to user profile
     */
    fun listenUserUpdate(uid: String) {
        val result = userRepo.listenUserUpdate(uid)
       userUpdateListener = result.addSnapshotListener{ snapshot, exception ->
            if (exception != null) {
                profileDataResult.value = DataResult(error = R.string.fetching_user_error)
                Log.w(TAG, "Listen failed.", exception)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                profileDataResult.value = DataResult(data = snapshot.toObject(User::class.java))

            } else {
                profileDataResult.value = DataResult(error = R.string.fetching_user_error)
                Log.d(TAG, "Document data: null")
            }
        }
    }

    /**
     * Remove listener on user profile
     */
    fun detachUserUpdateListener(){
        userUpdateListener?.remove()
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
                    is Result.Success ->  updatedProfile.value = DataResult(data = BaseRepository.SUCCESS_TASK)
                    is Result.Error -> updatedProfile.value = DataResult(error = R.string.profile_update_fail)
                    is Result.Canceled -> updatedProfile.value = DataResult(error = R.string.profile_update_cancel)
                }
            }
        } else {
            updatedProfile.value = DataResult(error = R.string.profile_update_fail)
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
     * Upload image to remote Storage Firebase
     * @param userId String that corresponding to user uid
     * @param drawable image to upload
     * @param fileName name of image file
     * Result is pass to uploadedImage liveData
     * */
    fun uploadImageFile(userId: String, drawable: Drawable, fileName: String){
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = storageRepo.uploadImageFile(userId, drawable, fileName)){
                is Result.Success -> uploadedImage.value = DataResult(data = result.data)
                is Result.Error ->  uploadedImage.value = DataResult(error = R.string.store_image_task_error)
                is Result.Canceled ->  uploadedImage.value = DataResult(error = R.string.store_image_task_cancel)
            }
        }
    }

    /**
     * Check if all value in Map<K,V> are correctly formatted
     * @return isValid Boolean
     */
    private fun isProfileFieldsValid(map: Map<String, Any?>): Boolean{
        var isValid = true
        map.keys.forEach {
            when (it){
                User.PROFILE_IMAGE_FIELD,
                User.DESCRIPTION_FIELD -> if (map[it] !is String?) {
                    Log.w(TAG, "'${User.DESCRIPTION_FIELD}' must be null or a String")
                    isValid = false
                    return@forEach
                }
                User.DISPLAY_NAME_FIELD,
                User.MUSIC_STYLE_FIELD -> if (map[it] !is String) {
                    Log.w(TAG, "'${User.DESCRIPTION_FIELD}' must be not null String")
                    isValid = false
                    return@forEach
                }
                User.GALLERY_FIELD -> if (map[it] !is StorageReference) {
                    Log.w(TAG, "'${User.DESCRIPTION_FIELD}' must be an instance of StorageReference")
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