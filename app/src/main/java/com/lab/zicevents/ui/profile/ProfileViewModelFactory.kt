package com.lab.zicevents.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lab.zicevents.data.database.publication.PublicationDataSource
import com.lab.zicevents.data.database.publication.PublicationRepository
import com.lab.zicevents.data.database.user.UserDataSource
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.storage.StorageDataSource
import com.lab.zicevents.data.storage.StorageRepository

/**
 * ViewModel provider factory instantiate LoginViewModel.
 */

class ProfileViewModelFactory: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                userRepo = UserRepository(
                    userDataSource = UserDataSource()
                ),
                publicationRepo = PublicationRepository(
                    publicationDataSource = PublicationDataSource()
                ),
                storageRepo = StorageRepository(
                    storageDataSource = StorageDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
