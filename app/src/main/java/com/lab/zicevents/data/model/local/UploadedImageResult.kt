package com.lab.zicevents.data.model.local

import android.net.Uri

data class UploadedImageResult(
    val requestId: Int,
    val imageUri: Uri? = null,
    val error: Int? = null
)
