package com.lab.zicevents.data.database.publication

import com.google.firebase.firestore.QuerySnapshot
import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.utils.base.BaseRepository
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.database.publication.PublicationDataSource

class PublicationRepository(private val publicationDataSource: PublicationDataSource) : BaseRepository() {
    /**
     * Create publication. Transform Task to Kotlin Coroutine
     * @param publication Publication.class that corresponding to publication information's
     * @return Result<Void>
     */
    suspend fun createPublication(publication: Publication) : Result<Void>{
        return when (val result = publicationDataSource.createPublication(publication).awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Get All User's Publications. Transform Task to Kotlin Coroutine
     * @param userId String that corresponding to user uid
     * @return Result<DocumentSnapshot>
     */
    suspend fun getUserPublications(userId : String) : Result<QuerySnapshot>{
        return when (val result = publicationDataSource.getUserPublications(userId).awaitTask()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }
}