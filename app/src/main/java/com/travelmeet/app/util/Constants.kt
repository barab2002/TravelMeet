package com.travelmeet.app.util

import com.travelmeet.app.BuildConfig

object Constants {
    val WEATHER_API_KEY: String = BuildConfig.WEATHER_API_KEY

    // Firebase Storage paths
    const val STORAGE_PROFILE_IMAGES = "profile_images"
    const val STORAGE_SPOT_IMAGES = "spot_images"

    // Firestore collection names
    const val COLLECTION_USERS = "users"
    const val COLLECTION_SPOTS = "spots"

    // Image compression
    const val MAX_IMAGE_SIZE_KB = 500
    const val IMAGE_QUALITY = 80

    // Permissions request codes
    const val LOCATION_PERMISSION_REQUEST = 1001
    const val CAMERA_PERMISSION_REQUEST = 1002
    const val STORAGE_PERMISSION_REQUEST = 1003
}
