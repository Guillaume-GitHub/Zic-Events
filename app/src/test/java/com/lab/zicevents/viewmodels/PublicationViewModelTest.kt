package com.lab.zicevents.viewmodels

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lab.zicevents.data.database.publication.PublicationDataSource
import com.lab.zicevents.data.database.publication.PublicationRepository
import com.lab.zicevents.data.database.user.UserDataSource
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.model.local.DataResult
import com.lab.zicevents.data.storage.StorageDataSource
import com.lab.zicevents.data.storage.StorageRepository
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.mock
import org.junit.Rule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.data.model.local.PublicationListResult
import com.lab.zicevents.ui.publication.PublicationViewModel
import com.lab.zicevents.utils.base.BaseRepository
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList


class PublicationViewModelTest {
    private lateinit var publicationViewModel: PublicationViewModel
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
    fun init() {

        try {
            FirebaseApp.initializeApp(context, mockedFirebaseOptions)
        } catch (error: IllegalStateException) {
            Log.d(this::class.java.simpleName, "FirebaseApp already initialized")
        }

        publicationViewModel = PublicationViewModel(
            userRepo = UserRepository(
                userDataSource = mockedUserSource
            ),
            storageRepo = StorageRepository(
                storageDataSource = mockedStorageSource
            ),
            publicationRepo = PublicationRepository(
                publicationDataSource = mockedPubliSource
            )
        )
    }

    @Test
    // Given no value set to LiveData
    // When get value of LiveData
    // Then value equal null
    fun checkProfileLiveDataInitialState() {
        assertNull(publicationViewModel.profileResult.value)
    }

    @Test
    // Given no value set to  LiveData
    // When send User to LiveData value
    // Then value equal User
    fun checkProfileLiveDataReturnUser() {
        val user = com.lab.zicevents.data.model.database.user.User(
            userId = "12134",
            displayName = "UserTest",
            pseudo = "PseudoTest",
            docRef = "/DocRef"
        )

        publicationViewModel.profileResult.observeForever { }
        publicationViewModel.profile.postValue(DataResult(data = user))

        val liveDataResult = publicationViewModel.profileResult.value

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

        publicationViewModel.profileResult.observeForever { }
        publicationViewModel.profile.postValue(DataResult(error = text))

        val liveDataResult = publicationViewModel.profileResult.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.data)
        assertEquals(text, liveDataResult.error!!)
    }

    /**************************/

    @Test
    // Given no value set to LiveData
    // When get value of LiveData
    // Then value equal null
    fun checkPublicationResultLiveData() {
        assertNull(publicationViewModel.publicationList.value)
    }

    @Test
    // Given no value set to LiveData
    // When send User to LiveData value
    // Then value equal Publication
    fun checkPublicationLiveDataReturnPublicationArrayList() {
        val publication = Publication(
            createdDate = Date.from(Instant.now()),
            userId = "123456",
            mediaUrl = "https://www.urltest.com",
            message = "publication message text",
            user = null
        )
        val publicationList = ArrayList<Publication>()
        publicationList.add(publication)

        publicationViewModel.publications.postValue(DataResult(data = publicationList))
        publicationViewModel.publicationList.observeForever { }

        val liveDataResult = publicationViewModel.publicationList.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.error)
        assertEquals(
            publication, (liveDataResult.data!! as ArrayList<*>)[0]
        )
    }

    @Test
    // Given user is null and error not null
    // When get value of profileResult LiveData
    // Then profileResult value error noy null
    fun checkPublicationLiveDataReturnError() {
        val text = R.string.fetching_user_canceled

        publicationViewModel.publications.postValue(DataResult(error = text))
        publicationViewModel.publicationList.observeForever { }

        val liveDataResult = publicationViewModel.publicationList.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.data)
        assertEquals(text, liveDataResult.error!!)
    }

    /**************************/

    @Test
    // Given no value set to LiveData
    // When get value of LiveData
    // Then value equal null
    fun checkNewPublicationResultLiveData() {
        assertNull(publicationViewModel.newPublications.value)
    }

    @Test
    // Given no value set to LiveData
    // When send User to LiveData value
    // Then value equal Publication
    fun checkNewPublicationLiveDataReturnPublicationArrayList() {
        val publication = Publication(
            createdDate = Date.from(Instant.now()),
            userId = "123456",
            mediaUrl = "https://www.urltest.com",
            message = "publication message text",
            user = null
        )

        val publicationList = ArrayList<Publication>()
        publicationList.add(publication)

        publicationViewModel.newPublications.postValue(DataResult(data = publicationList))
        publicationViewModel.newPublicationList.observeForever { }

        val liveDataResult = publicationViewModel.newPublicationList.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.error)
        assertEquals(
            publication, (liveDataResult.data!! as ArrayList<*>)[0]
        )
    }

    @Test
    // Given user is null and error not null
    // When get value of profileResult LiveData
    // Then profileResult value error noy null
    fun checkNewPublicationLiveDataReturnError() {
        val text = R.string.fetch_user_publication_error

        publicationViewModel.newPublications.postValue(DataResult(error = text))
        publicationViewModel.newPublicationList.observeForever { }

        val liveDataResult = publicationViewModel.newPublicationList.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.data)
        assertEquals(text, liveDataResult.error!!)
    }

    /**************************/

    @Test
    // Given no value set to LiveData
    // When get value of LiveData
    // Then value equal null
    fun checkUserPublicationResultLiveData() {
        assertNull(publicationViewModel.userPublicationList.value)
    }

    @Test
    // Given no value set to LiveData
    // When send User to LiveData value
    // Then value equal Publication
    fun checkUserPublicationLiveDataReturnPublicationListResult() {
        val publication = Publication(
            createdDate = Date.from(Instant.now()),
            userId = "123456",
            mediaUrl = "https://www.urltest.com",
            message = "publication message text",
            user = null
        )

        val publicationList = ArrayList<Publication>()
        publicationList.add(publication)

        val publicationListResult = PublicationListResult(
            list = publicationList,
            error = null
        )

        publicationViewModel.userPublicationList.postValue(publicationListResult)
        publicationViewModel.newPublications.observeForever { }

        val liveDataResult = publicationViewModel.userPublications.value

        assertNotNull(liveDataResult)
        assertEquals(
            publication, (liveDataResult!!.list as ArrayList<*>)[0]
        )
    }

    @Test
    // Given user is null and error not null
    // When get value of profileResult LiveData
    // Then profileResult value error noy null
    fun checkUserPublicationLiveDataReturnError() {
        val text = R.string.fetch_user_publication_error

        val publicationListResult = PublicationListResult(
            error = text
        )

        publicationViewModel.userPublicationList.postValue(publicationListResult)
        publicationViewModel.userPublications.observeForever { }

        val liveDataResult = publicationViewModel.userPublications.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.list)
        assertEquals(text, liveDataResult.error!!)
    }

    /**************************/

    @Test
    // Given no value set to LiveData
    // When get value of LiveData
    // Then value equal null
    fun checkPublicationValidationResultLiveData() {
        assertNull(publicationViewModel.publicationValid.value)
    }

    @Test
    // Given no value set to LiveData
    // When send User to LiveData value
    // Then value equal Publication
    fun checkPublicationValidationLiveDataReturnBoolean() {
        publicationViewModel.publicationValidation.observeForever { }

        publicationViewModel.publicationValid.postValue(true)
        assertTrue(publicationViewModel.publicationValidation.value!!)

        publicationViewModel.publicationValid.postValue(false)
        assertFalse(publicationViewModel.publicationValidation.value!!)
    }

    /**************************/

    @Test
    // Given no value set to LiveData
    // When get value of LiveData
    // Then value equal null
    fun checkPublicationCreationSateResultLiveData() {
        assertNull(publicationViewModel.publicationCreationSate.value)
    }

    @Test
    // Given no value set to LiveData
    // When send User to LiveData value
    // Then value equal Publication
    fun checkPublicationCreationSateReturnSuccessTask() {
        publicationViewModel.publicationCreationSate.observeForever { }
        publicationViewModel.addPublication.postValue(DataResult(data = BaseRepository.SUCCESS_TASK))

        val liveDataResult = publicationViewModel.publicationCreationSate.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.error)
        assertEquals(BaseRepository.SUCCESS_TASK, liveDataResult.data as Int)
    }

    @Test
    // Given user is null and error not null
    // When get value of profileResult LiveData
    // Then profileResult value error noy null
    fun checkPublicationCreationSateLiveDataReturnError() {
        val text = R.string.post_publication_error

        publicationViewModel.publicationCreationSate.observeForever { }
        publicationViewModel.addPublication.postValue(DataResult(error = text))

        val liveDataResult = publicationViewModel.publicationCreationSate.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.data)
        assertEquals(text, liveDataResult.error!!)
    }

    @Test
    fun checkUserIsAlreadyFollow() {
        val userId = "67890"

        val followedIdList = ArrayList<String>()
        followedIdList.add(userId)

        val authUser = com.lab.zicevents.data.model.database.user.User(
            userId = "12345",
            displayName = "UserTest",
            pseudo = "PseudoTest",
            docRef = "/DocRef",
            subscriptions = followedIdList
        )
        // check if userId is present in authUser.subscriptions list
        assertTrue(publicationViewModel.isAlreadyFollow(userId, authUser)!!)
    }

    @Test
    fun checkUserIsNotAlreadyFollow() {
        val userId = "67890"

        val authUser = com.lab.zicevents.data.model.database.user.User(
            userId = "12345",
            displayName = "UserTest",
            pseudo = "PseudoTest",
            docRef = "/DocRef"
        )
        // check if userId is not present in authUser.subscriptions list
        assertFalse(publicationViewModel.isAlreadyFollow(userId, authUser)!!)
    }

    @Test
    fun checkUserIsAlreadyFollowWithNullAuthUser() {
        val userId = "67890"
        assertNull(publicationViewModel.isAlreadyFollow(userId, null))
    }
}