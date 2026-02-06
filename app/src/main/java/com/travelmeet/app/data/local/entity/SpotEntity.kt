package com.travelmeet.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spots")
data class SpotEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val username: String,
    val userPhotoUrl: String?,
    val title: String,
    val imageUrl: String,
    val localImagePath: String?,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val locationName: String?,
    val timestamp: Long,
    val likesCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false
)
