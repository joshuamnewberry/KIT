package edu.gvsu.cis.kit

import androidx.compose.runtime.Composable

interface CameraManager {
    fun launchCamera()
}

@Composable
expect fun rememberCameraManager(onImageCaptured: (ByteArray?) -> Unit): CameraManager