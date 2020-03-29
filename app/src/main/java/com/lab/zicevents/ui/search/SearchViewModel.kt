package com.lab.zicevents.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lab.zicevents.R
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.data.model.local.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchViewModel(private val userRepo: UserRepository) : ViewModel() {

    val userSearch = MutableLiveData<DataResult>()
    val searchUsersResult: LiveData<DataResult> = userSearch

    fun searchUsers(query: String){
        GlobalScope.launch(Dispatchers.Main) {
            when (val result = userRepo.searchUsers(query)) {
                is Result.Success ->
                    userSearch.value = DataResult(data = result.data.toObjects(User::class.java))
                is Result.Error ->
                    userSearch.value = DataResult(error = R.string.error_when_fetch_user)
                is Result.Canceled ->
                    DataResult(error = R.string.operation_canceled)
            }
        }
    }
}