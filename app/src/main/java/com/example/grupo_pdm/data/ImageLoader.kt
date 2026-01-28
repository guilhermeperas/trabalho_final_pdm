package com.example.grupo_pdm.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageLoader(private val scope: CoroutineScope) {

    fun loadMoviePicture(movieId: Int, pictureId: Int, imageView: android.widget.ImageView) {
        // Tag value to ensure we are loading into the correct view (handling recycling)
        val tagValue = "$movieId:$pictureId"
        imageView.tag = tagValue
        imageView.setImageResource(android.R.drawable.ic_menu_report_image) // Placeholder

        scope.launch {
            // 1. Fetch bytes
            /*
            val bytes = MovieServiceClient.getMoviePictureBytes(movieId, pictureId)

            // 2. Decode and Set Bitmap if view is still bound to this data
            if (bytes != null && imageView.tag == tagValue) {
                // Decode on Default dispatcher
                val bmp = withContext(Dispatchers.Default) {
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }

                // Set on Main thread
                if (imageView.tag == tagValue && bmp != null) {
                    imageView.setImageBitmap(bmp)
                }
            }
            
             */
        }
    }
}