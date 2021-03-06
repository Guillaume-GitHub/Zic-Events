package com.lab.zicevents.ui.profile

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.StorageReference
import com.lab.zicevents.R
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.api.geocoding.GeocodingRepository
import com.lab.zicevents.data.database.publication.PublicationRepository
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.model.api.geocoding.Address
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.data.model.local.DataResult
import com.lab.zicevents.data.model.local.PublicationListResult
import com.lab.zicevents.data.model.local.UploadedImageResult
import com.lab.zicevents.data.storage.StorageRepository
import com.lab.zicevents.utils.base.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class ProfileViewModel(private val userRepo: UserRepository,
                       private val publicationRepo: PublicationRepository,
                       private val storageRepo: StorageRepository,
                       private val geocodingRepo: GeocodingRepository)
    : ViewModel() {

    private val TAG = this::class.java.simpleName
    private var userUpdateListener: ListenerRegistration? = null
    val dateFormat: DateFormat = SimpleDateFormat.getDateInstance()

    val profileDataResult = MutableLiveData<DataResult>()
    val userProfileResult: LiveData<DataResult> = profileDataResult

    val uploadedImage = MutableLiveData<UploadedImageResult>()
    val uploadImageResult: LiveData<UploadedImageResult> = uploadedImage

    val publicationList = MutableLiveData<PublicationListResult>()
    val userPublications: LiveData<PublicationListResult> = publicationList

    val updatedProfile = MutableLiveData<DataResult>()
    val updateProfileResult: LiveData<DataResult> = updatedProfile

    val geocodingAddress = MutableLiveData<DataResult>()
    val geocodingResult: LiveData<DataResult> = geocodingAddress

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
     * @param requestId isdentifier of
     * Result is pass to uploadedImage liveData
     * */
    fun uploadImageFile(userId: String, drawable: Drawable, fileName: String, requestId: Int){
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = storageRepo.uploadImageFile(userId, drawable, fileName)){
                is Result.Success -> uploadedImage.value = UploadedImageResult(requestId = requestId
                    , imageUri = result.data)
                is Result.Error ->  uploadedImage.value = UploadedImageResult(requestId = requestId
                    ,error = R.string.store_image_task_error)
                is Result.Canceled ->  uploadedImage.value = UploadedImageResult(requestId = requestId
                    ,error = R.string.store_image_task_cancel)
            }
        }
    }

    /**
     * Get address components like geometry from a non formatted address string
     * @param address address taped by user
     * @param key Geocoding api key
     */
    fun getGeolocationAddress(address: String) {
        GlobalScope.launch(Dispatchers.Main) {
            when(val result = geocodingRepo.getGeolocationAddress(address)){
                is Result.Success -> {
                    val adr = result.data.results
                    if (!adr.isNullOrEmpty()) {
                        geocodingAddress.value = DataResult(data = adr[0])
                    }else {
                        geocodingAddress.value = DataResult(data = null)
                    }
                }
                is Result.Error ->  geocodingAddress.value = DataResult(error = R.string.geocoding_address_error)
                is Result.Canceled ->  geocodingAddress.value =  DataResult(error = R.string.operation_canceled)
            }
        }
    }

    /**
     * Check if all value in Map<K,V> are correctly formatted
     * @return isValid Boolean
     */
    fun isProfileFieldsValid(map: Map<String, Any?>): Boolean{
        var isValid = true
        map.keys.forEach {
            when (it){
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
                User.MUSIC_STYLE_FIELD -> if (map[it] !is ArrayList<*>?) {
                    Log.w(TAG, "'${User.MUSIC_STYLE_FIELD}' must be an ArrayList of String or null")
                    isValid = false
                    return@forEach
                }
                User.GALLERY_FIELD -> if (map[it] !is StorageReference) {
                    Log.w(TAG, "'${User.DESCRIPTION_FIELD}' must be an instance of StorageReference")
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

    /**
     * Return formatted Chip view
     * @param context simple context
     * @param chipText text of chip
     * @param isEditable set true to add close icon to chip
     * @param clickListener callback to triggered close button click
     * @return Chip view
     */
    fun getFormattedChip(context: Context,
                         chipText: String,
                         isEditable: Boolean = false ,
                         clickListener: View.OnClickListener? = null): Chip {

        val chip = Chip(context)
        chip.apply {
            if (isEditable) closeIcon = context.resources.getDrawable(R.drawable.ic_close_white_24dp)
            clickListener?.let { chip.setOnCloseIconClickListener(it) }
            isCloseIconVisible = isEditable
            setChipBackgroundColorResource(R.color.colorAccent)
            setOnCloseIconClickListener(clickListener)
            text = chipText
        }
        return chip
    }

    /**
     * Check if user description matches with description length
     * @param description simple user description
     * @return true if ok or false
     */
    fun isDescriptionValid(description: String?): Boolean{
        return when {
            description.isNullOrBlank() -> false
            description.length in (20..150) -> true
            else -> false
        }
    }

    /**
     * Remove all whitespace in string
     * @param text you want to transform
     * @return text without whitespaces
     */
    fun formattedMusicStyle(text: String): String {
        return text.replace(" ","")
    }
}