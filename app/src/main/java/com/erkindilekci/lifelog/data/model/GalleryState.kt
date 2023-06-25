package com.erkindilekci.lifelog.data.model

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

class GalleryState {
    val images = mutableStateListOf<GalleryImage>()
    val imagesToBeDeleted = mutableStateListOf<GalleryImage>()

    fun addImage(galleryImage: GalleryImage) {
        images.add(galleryImage)
    }

    fun deleteImage(galleryImage: GalleryImage) {
        images.remove(galleryImage)
        imagesToBeDeleted.remove(galleryImage)
    }

    fun clearImagesToBeDeleted() {
        imagesToBeDeleted.clear()
    }
}

data class GalleryImage(
    val image: Uri,
    val remoteImagePath: String = ""
)

@Composable
fun rememberGalleryState(): GalleryState {
    return remember { GalleryState() }
}
