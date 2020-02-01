package com.lab.zicevents.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.OutputStream

class ImagePickerHelper {
    companion object {
        const val PROFILE_IMG_RQ = 2000
        const val COVER_IMG_RQ = 2010

        const val MAX_SIZE = 512 // approximately 250KB

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
        fun getByteArray(drawable: Drawable): ByteArray{
            var bitmap = (drawable as BitmapDrawable).bitmap

            if (bitmap.height >= MAX_SIZE || bitmap.width >= MAX_SIZE) {
                Log.d(this::class.java.simpleName,"image = ${bitmap.width} X ${bitmap.height} px")
                bitmap = resizeBitmap(bitmap)
            }

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos as OutputStream)
            return baos.toByteArray()
        }

        /**
         * Resize Image
         * @param bitmap is bitmap you want to resize
         * @return resized bitmap
         */
        private fun resizeBitmap(bitmap: Bitmap): Bitmap{
            // Get Scale
            val scale: Double =
                if (bitmap.width >= bitmap.height)
                    (MAX_SIZE.toDouble() / bitmap.width.toDouble())
                else
                    (MAX_SIZE.toDouble() / bitmap.height.toDouble())

            // RESIZE IMAGE
            val resizedHeight = (bitmap.height * scale).toInt()
            val resizeWidth = (bitmap.width * scale).toInt()

            Log.d(this::class.java.simpleName,"Resized image = $resizeWidth X $resizedHeight px")
            return Bitmap.createScaledBitmap(bitmap, resizeWidth, resizedHeight, false)
        }

        /**
         * Try to get Drawable image from Uri
         * @param context context
         * @param imageUri uri object
         * @return Drawable image or null
         */
        fun getDrawableFromUri(context: Context?, imageUri: Uri): Drawable? {
            return try {
                val stream = context?.contentResolver?.openInputStream(imageUri)
                Drawable.createFromStream(stream, imageUri.toString())
            } catch (e: FileNotFoundException){
                Log.w(this::class.java.simpleName, "", e)
                null
            }
        }
    }
}