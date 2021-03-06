package com.lab.zicevents.ui.publication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lab.zicevents.data.database.publication.PublicationDataSource
import com.lab.zicevents.data.database.publication.PublicationRepository
import com.lab.zicevents.data.database.user.UserDataSource
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.storage.StorageDataSource
import com.lab.zicevents.data.storage.StorageRepository

class PublicationViewModelFactory: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublicationViewModel::class.java)) {
            return PublicationViewModel(publicationRepo = PublicationRepository(
                publicationDataSource =  PublicationDataSource()
            ),
                userRepo = UserRepository(
                    userDataSource = UserDataSource()
                ),
                storageRepo = StorageRepository(
                    storageDataSource = StorageDataSource()
                )
            )
            as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}