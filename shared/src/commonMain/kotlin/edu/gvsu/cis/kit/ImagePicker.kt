package edu.gvsu.cis.kit

import androidx.compose.runtime.Composable

interface ImagePickerManager {
    fun launchImagePicker()
}

@Composable
expect fun rememberImagePickerManager(onImagePicked: (ByteArray?) -> Unit): ImagePickerManager