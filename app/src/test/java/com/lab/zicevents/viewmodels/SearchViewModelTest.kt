package com.lab.zicevents.viewmodels

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.R
import com.lab.zicevents.data.database.user.UserDataSource
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.model.local.DataResult
import com.lab.zicevents.ui.search.SearchViewModel
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.mockito.Mockito.mock

class SearchViewModelTest {

    private lateinit var searchViewModel: SearchViewModel
    private val context = mock(Context::class.java)
    private val mockedFirebaseOptions =
        FirebaseOptions.Builder().setApplicationId("zic-events").build()
    private val mockedFirestore = mock(FirebaseFirestore::class.java)
    private val mockedUserSource = UserDataSource(mockedFirestore)


    @get:Rule // -> allows liveData to work on different thread
    val executorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        try {
            FirebaseApp.initializeApp(context, mockedFirebaseOptions)
        } catch (error: IllegalStateException) {
            Log.d(this::class.java.simpleName, "FirebaseApp already initialized")
        }

        searchViewModel =
            SearchViewModel(userRepo = UserRepository(userDataSource = mockedUserSource))
    }

    @Test
    fun checkSearchUsersResultLiveDataInitialState() {
        assertNull(searchViewModel.searchUsersResult.value)
    }

    @Test
    // Given no value set to  LiveData
    // When send User to LiveData value
    // Then value equal User
    fun checkSearchUsersLiveDataReturnUserList() {
        val user1 = User(
            userId = "12134",
            displayName = "UserTest",
            pseudo = "PseudoTest",
            docRef = "/DocRef"
        )

        val user2 = User(
            userId = "56789",
            displayName = "UserTest2",
            pseudo = "PseudoTest2",
            docRef = "/DocRef2"
        )

        val userList = ArrayList<User>()
        userList.add(0, user1)
        userList.add(1, user2)

        searchViewModel.searchUsersResult.observeForever { }
        searchViewModel.userSearch.postValue(DataResult(data = userList))

        val liveDataResult = searchViewModel.searchUsersResult.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.error)
        assertEquals(userList, liveDataResult.data!!)
    }

    @Test
    // Given user is null and error not null
    // When get value of LiveData
    // Then value error not null
    fun checkUserLiveDataReturnError() {
        val error = R.string.error_when_fetch_user

        searchViewModel.searchUsersResult.observeForever { }
        searchViewModel.userSearch.postValue(DataResult(error = error))

        val liveDataResult = searchViewModel.searchUsersResult.value

        assertNotNull(liveDataResult)
        assertNull(liveDataResult!!.data)
        assertEquals(error, liveDataResult.error!!)
    }
}