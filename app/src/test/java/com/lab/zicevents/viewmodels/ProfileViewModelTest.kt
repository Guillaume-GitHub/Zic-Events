package com.lab.zicevents.viewmodels

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.material.chip.Chip
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.data.api.geocoding.GeocodingDataSource
import com.lab.zicevents.data.api.geocoding.GeocodingRepository
import com.lab.zicevents.data.database.publication.PublicationDataSource
import com.lab.zicevents.data.database.publication.PublicationRepository
import com.lab.zicevents.data.database.user.UserDataSource
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.model.local.DataResult
import com.lab.zicevents.data.model.local.UploadedImageResult
import com.lab.zicevents.data.storage.StorageDataSource
import com.lab.zicevents.data.storage.StorageRepository
import com.lab.zicevents.ui.profile.ProfileViewModel
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import java.text.SimpleDateFormat

class ProfileViewModelTest {
    private lateinit var profileViewModel: ProfileViewModel
    private val context = mock(Context::class.java)
    private val mockedFirebaseOptions =
        FirebaseOptions.Builder().setApplicationId("zic-events").build()
    private val mockedFirestore = mock(FirebaseFirestore::class.java)
    private val mockedStorage = mock(FirebaseStorage::class.java)
    private val mockedUserSource = UserDataSource(mockedFirestore)
    private val mockedStorageSource = StorageDataSource(mockedStorage)
    private val mockedPubliSource = PublicationDataSource(mockedFirestore)

    @get:Rule // -> allows liveData to work on different thread
    val executorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        try {
            FirebaseApp.initializeApp(context, mockedFirebaseOptions)
        } catch (error: IllegalStateException) {
            Log.d(this::class.java.simpleName, "FirebaseApp already initialized")
        }

        profileViewModel = ProfileViewModel(
            userRepo = UserRepository(
                userDataSource = mockedUserSource
            ),
            storageRepo = StorageRepository(
                storageDataSource = mockedStorageSource
            ),
            publicationRepo = PublicationRepository(
                publicationDataSource = mockedPubliSource
            ),
            geocodingRepo = GeocodingRepository(
                geocodingDataSource = GeocodingDataSource()
            )
        )
    }

    @Test
    fun checkDateFormat() {
        val dateFormat = SimpleDateFormat.getDateInstance()
        assertEquals(dateFormat, profileViewModel.dateFormat)
    }

    /*****************/

    @Test
    fun checkIsDescriptionValidLessThan20Char() {
        val text = "Less 20 char text"
        assertFalse(profileViewModel.isDescriptionValid(text))
    }

    @Test
    fun checkIsDescriptionValidMoreThan150Char() {
        // 155 char length
        val text = "Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum " +
                "Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum"
        assertFalse(profileViewModel.isDescriptionValid(text))
    }

    @Test
    fun checkIsDescriptionValidBetween20And150Char() {
        // 28 char length
        val text = "This is a simple description"
        assertTrue(profileViewModel.isDescriptionValid(text))
    }

    @Test
    fun checkIsDescriptionValidWithNullString() {
        assertFalse(profileViewModel.isDescriptionValid(null))
    }

    /*****************/

    @Test
    fun checkFormattedMusicStyleWithWhiteSpace(){
        val textWithWhiteSpace = "Text    with some white space"
        val textWithoutWhiteSpace = "Textwithsomewhitespace"
        assertEquals(textWithoutWhiteSpace, profileViewModel.formattedMusicStyle(textWithWhiteSpace))
    }

    @Test
    fun checkFormattedMusicStyleWithoutWhiteSpace(){
        val textWithoutWhiteSpace = "Textwithsomewhitespace"
        assertEquals(textWithoutWhiteSpace, profileViewModel.formattedMusicStyle(textWithoutWhiteSpace))
    }

    @Test
    fun checkFormattedMusicStyleWithSpecialChars(){
        val textWithWhiteSpace = "Text  -  with some white / space"
        val textWithoutWhiteSpace = "Text-withsomewhite/space"
        assertEquals(textWithoutWhiteSpace, profileViewModel.formattedMusicStyle(textWithWhiteSpace))
    }

    /*****************/

    @Test
    fun checkIsProfileFieldsValidWithValidPramsAndValues(){
        val description = "short description"
        val descriptionParam = User.DESCRIPTION_FIELD

        val musicStyleList = arrayListOf("Rock", "Hard Rock", "Metal")
        val musicStyleParam = User.MUSIC_STYLE_FIELD

        val updates = HashMap<String, Any?>()
        updates[descriptionParam] = description
        updates[musicStyleParam] = musicStyleList

        assertTrue(profileViewModel.isProfileFieldsValid(updates))
    }

    @Test
    fun checkIsProfileFieldsValidWithValidPramsInvalidValues(){
        val description = 35458 // Expected String instead of Int
        val descriptionParam = User.DESCRIPTION_FIELD

        val musicStyleList = arrayListOf("Rock", "Hard Rock", "Metal")
        val musicStyleParam = User.MUSIC_STYLE_FIELD

        val updates = HashMap<String, Any?>()
        updates[descriptionParam] = description
        updates[musicStyleParam] = musicStyleList

        assertFalse(profileViewModel.isProfileFieldsValid(updates))
    }

    @Test
    fun checkIsProfileFieldsValidWithInvalidPram(){
        val paramValue = 10
        val invalidParam = "numberOfFollowers"

        val musicStyleList = arrayListOf("Rock", "Hard Rock", "Metal")
        val musicStyleParam = User.MUSIC_STYLE_FIELD

        val updates = HashMap<String, Any?>()
        updates[invalidParam] = paramValue
        updates[musicStyleParam] = musicStyleList

        assertFalse(profileViewModel.isProfileFieldsValid(updates))
    }

    /*****************/

    @Test
    // Given no value set to LiveData
    // When get value of LiveData
    // Then value equal null
    fun checkProfileLiveDataInitialState() {
        assertNull(profileViewModel.userProfileResult.value)
    }

    @Test
    // Given no value set to  LiveData
    // When send User to LiveData value
    // Then value equal User
    fun checkProfileLiveDataReturnUser() {
        val user = User(
            userId = "12134",
            displayName = "UserTest",
            pseudo = "PseudoTest",
            docRef = "/DocRef"
        )

        profileViewModel.userProfileResult.observeForever { }
        profileViewModel.profileDataResult.postValue(DataResult(data = user))

        val liveDataResult = profileViewModel.userProfileResult.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.error)
        assertEquals(user, liveDataResult.data!!)
    }

    @Test
    // Given user is null and error not null
    // When get value of LiveData
    // Then value error not null
    fun checkUserLiveDataReturnError() {
        val text = R.string.error_when_fetch_user

        profileViewModel.userProfileResult.observeForever { }
        profileViewModel.profileDataResult.postValue(DataResult(error = text))

        val liveDataResult = profileViewModel.userProfileResult.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.data)
        assertEquals(text, liveDataResult.error!!)
    }

    /*****************/

    @Test
    fun checkUploadImageResultLiveDataInitialState() {
        assertNull(profileViewModel.uploadImageResult.value)
    }

    @Test
    fun checkUploadImageResultLiveDataReturnUploadedImageResult() {
        val imageUri = mock(Uri::class.java)
        val requestCode = 10

        profileViewModel.uploadImageResult.observeForever { }
        profileViewModel.uploadedImage.postValue(
            UploadedImageResult(
                requestId = requestCode,
                imageUri = imageUri
            )
        )

        val liveDataResult = profileViewModel.uploadImageResult.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.error)
        assertEquals(requestCode, liveDataResult.requestId)
        assertEquals(imageUri, liveDataResult.imageUri)
    }

    @Test
    fun checkUploadImageResultDataReturnError() {
        val error = R.string.store_image_task_error

        profileViewModel.uploadImageResult.observeForever { }
        profileViewModel.uploadedImage.postValue(
            UploadedImageResult(
                requestId = 10,
                error = error
            )
        )

        val liveDataResult = profileViewModel.uploadImageResult.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.imageUri)
        assertEquals(error, liveDataResult.error!!)
        assertEquals(10, liveDataResult.requestId)
    }
}