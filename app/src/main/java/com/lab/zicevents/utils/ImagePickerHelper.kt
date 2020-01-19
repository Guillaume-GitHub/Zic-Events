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
        const val PROFILE_IMG_RQ = 2000
        const val BACKGROUND_IMG_RQ = 2010

        const val PROFILE_SIZE = 180
        const val BACKGROUND_SIZE = 851

        /**
         * Launch Gallery Image Picker
         * @param fragment current fragment
         * @param requestCode request identifier
         */
        fun pickImageFromGallery(fragment: Fragment, requestCode: Int){
           val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
          fragment.startActivityForResult(intent, requestCode)
        }

        /**
         * Transform and Resize drawable to ByteArray
         * @param drawable simple drawable
         * @return ByteArray
         */
        fun getByteArray(drawable: Drawable, size: Int): ByteArray{
            val bitmap = (drawable as BitmapDrawable).bitmap

            // Get Scale
            val scale: Double =
                if (bitmap.height >= bitmap.height) (size.toDouble() / bitmap.height.toDouble())
                else (size.toDouble() / bitmap.width.toDouble())

            // RESIZE IMAGE
            val resizedHeight = (bitmap.height * scale).toInt()
            val resizeWidth = (bitmap.width * scale).toInt()
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, resizeWidth, resizedHeight, true)

            val baos = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            return baos.toByteArray()
        }

    }
}