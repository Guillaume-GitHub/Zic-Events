package com.lab.zicevents.ui.publication

import android.content.ContentValues.TAG
import android.graphics.drawable.Drawable
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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
import com.lab.zicevents.data.model.local.PublicationListResult
import com.lab.zicevents.data.storage.StorageRepository
import com.lab.zicevents.utils.base.BaseRepository
import java.util.*
import kotlin.collections.ArrayList

class PublicationViewModel(private val publicationRepo: PublicationRepository,
                           private val userRepo: UserRepository,
                           private val storageRepo: StorageRepository): ViewModel() {

    val authUser = FirebaseAuth.getInstance().currentUser

    private val publications = MutableLiveData<DataResult>()
    val publicationList: LiveData<DataResult> = publications

    private val newPublications = MutableLiveData<DataResult>()
    val newPublicationList: LiveData<DataResult> = newPublications

    private val profile = MutableLiveData<DataResult>()
    val profileResult: LiveData<DataResult> = profile

    private val userPublicationList = MutableLiveData<PublicationListResult>()
    val userPublications: LiveData<PublicationListResult> = userPublicationList

    private val publicationValid = MutableLiveData<Boolean>()
    val publicationValidation: LiveData<Boolean> = publicationValid

    private val addPublication = MutableLiveData<DataResult>()
    val publicationCreationSate: LiveData<DataResult> = addPublication

    /**
     * Fetch publication from database async
     * @param subscriptionList list of users's id to to filter research
     * @Return result as LiveData
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
     * Fetch publications after a specific date
     * @param subscriptionList list of users's id to to filter research
     * @param lastVisiblePublication most recent visible publication
     * Return result as LiveData
     */
    fun getLastSubscribedPublications(subscriptionList: List<String>, lastVisiblePublication: Publication) {
        if (lastVisiblePublication.createdDate != null){
            GlobalScope.launch(Dispatchers.Main) {
                when (val result = publicationRepo.getSubscribedPublications(subscriptionList, lastVisiblePublication)) {
                    is Result.Success -> {
                        val list : ArrayList<Publication> = ArrayList()
                        list.addAll(result.data.toObjects(Publication::class.java))
                        newPublications.value = DataResult(data = list)
                    }
                    is Result.Error -> {
                        Log.e(TAG, "Error when trying to get publications", result.exception)
                        newPublications.value = DataResult(error = R.string.fetching_publication_error)
                    }
                    is Result.Canceled -> {
                        Log.e(TAG, "Action canceled", result.exception)
                        newPublications.value = DataResult(error = R.string.operation_canceled)
                    }
                }
            }
        } else{
            newPublications.value = DataResult(error = R.string.fetching_publication_error)
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
                    userPublicationList.value = PublicationListResult(list = publications)
                }
                is Result.Error ->  userPublicationList.value = PublicationListResult(error = R.string.fetch_user_publication_error)
                is Result.Canceled ->  userPublicationList.value = PublicationListResult(error = R.string.fetch_user_publication_cancel)
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

    /**
     * Check if publication contain a message
     * @param message message of publication
     */
    fun publicationDataChange(message: Editable?) {
        publicationValid.value = !message.isNullOrBlank()
    }


    /**
     * Upload image to remote Storage Firebase and get uploaded image url
     * create publication with this image url or display error message if null
     * @param user current user profile
     * @param drawable image to upload
     * @param fileName name of image file to upload
     * @param message (publication message attach to image publication)
     * */
    private fun uploadImageFile(user: User, drawable: Drawable, fileName: String, message: String){
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = storageRepo.uploadImageFile(user.userId, drawable, fileName)){
                is Result.Success -> {
                    if (result.data != null)
                        createPublication(user,message, result.data.toString())
                    else
                        addPublication.value  = DataResult(error = R.string.store_image_task_error)
                }
                is Result.Error ->
                    addPublication.value  = DataResult(error = R.string.store_image_task_error)
                is Result.Canceled ->
                    addPublication.value  = DataResult(error = R.string.store_image_task_cancel)
            }
        }
    }

    /**
     * Check values before create publication
     * pass data to create publication method or display error
     * @param user user profile who create publication (may be null)
     * @param message message of publication (may be null)
     * @param image image drawable of publication (may be null)
     */
     fun postPublication(user: User?, message: Editable?, image: Drawable?){
        when {
            user == null ->
                addPublication.value = DataResult(error = R.string.error_when_fetch_user)
            message.isNullOrBlank() ->
                addPublication.value = DataResult(error = R.string.empty_publication_message)
            else -> {
                if (image != null)
                    uploadImageFile(user,image, UUID.randomUUID().toString(), message.toString())
                else
                    createPublication(user, message.toString())
            }
        }
    }

    /**
     * Create publication object and write it to database
     * @param user user profile who create publication
     * @param message string message of publication
     * @param imageUrl full http url of publication image (may be null)
     */
    private fun createPublication(user: User, message: String, imageUrl: String? = null){
        val publication = Publication(
            userId = user.userId,
            message = message,
            mediaUrl = imageUrl,
            user = userRepo.getUserDocRef(user.userId))

        GlobalScope.launch(Dispatchers.Main) {
            when (publicationRepo.createPublication(publication)){
                is Result.Success -> addPublication.value = DataResult(data = BaseRepository.SUCCESS_TASK)
                is Result.Error ->  addPublication.value  = DataResult(error = R.string.post_publication_error)
                is Result.Canceled ->  addPublication.value  = DataResult(error = R.string.operation_canceled)
            }
        }
    }

    /** Display details user profile fragment
     *
     */
    fun navigateToProfile(fragment:Fragment, userId: String){
        val action = PublicationFragmentDirections.fromPublicationToUserDetails(userId)
        findNavController(fragment).navigate(action)
    }
}

