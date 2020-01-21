package com.lab.zicevents.data.storage

import android.graphics.drawable.Drawable
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.lab.zicevents.utils.ImagePickerHelper

/**
 *  Upload/Download/Delete Firebase Storage
 */

class StorageDataSource {

    private val storage = FirebaseStorage.getInstance()
    private val IMAGE_EXTENSION = ".jpeg"

    //****************** UPLOAD *********************//
    /**
     * Upload image to remote Storage Firebase
     * @param userId String that corresponding to user uid
     * @param drawable image to upload
     * @param fileName name of image file
     * @return  UploadTask
     * */
    fun uploadImageFile(userId: String, drawable: Drawable, fileName: String) : UploadTask {
        val path = "${userId}/${fileName}${IMAGE_EXTENSION}"
        val storageRef = storage.getReference(path)
        return storageRef.putBytes(ImagePickerHelper.getByteArray(drawable))
    }
}

