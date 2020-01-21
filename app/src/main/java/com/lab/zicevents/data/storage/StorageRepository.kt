package com.lab.zicevents.data.storage

import android.graphics.drawable.Drawable
import android.net.Uri
import com.google.firebase.storage.StorageReference
import com.lab.zicevents.data.Result
import com.lab.zicevents.utils.base.BaseRepository

class StorageRepository(private val storageDataSource: StorageDataSource) : BaseRepository() {

    /**
     * Upload image to remote Storage Firebase
     * @param userId String that corresponding to user uid
     * @param drawable image to upload
     * @param fileName name of image file
     * @return  Result<StorageReference>
     * */
    suspend fun uploadImageFile(userId: String, drawable: Drawable, fileName: String) : Result<Uri?> {
        return when (val result = storageDataSource.uploadImageFile(userId, drawable, fileName).awaitUpload()){
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }
}