package com.lab.zicevents.utils

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import java.io.ByteArrayOutputStream

class ImagePickerHelper {
    companion object {
        const val IMAGE_PICKER_RQ = 2000

        fun pickImageFromGallery(fragment: Fragment){
           val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
          fragment.startActivityForResult(intent, IMAGE_PICKER_RQ)
        }

        fun getByteArray(drawable: Drawable): ByteArray{
            val bitmap = (drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            return baos.toByteArray()
        }

    }
}