package com.travelmeet.app.data.model

import com.travelmeet.app.util.TimeUtils

/** Data model representing a single comment on a spot. */
data class SpotComment(
    val id: String,
    val spotId: String,
    val userId: String,
    val username: String,
    val userPhotoUrl: String?,
    val text: String,
    val timestamp: Long
) {
    val relativeTime: String
        get() = TimeUtils.getRelativeTimeString(timestamp)
}

