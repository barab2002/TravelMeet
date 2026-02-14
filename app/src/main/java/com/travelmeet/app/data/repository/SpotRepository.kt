package com.travelmeet.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.travelmeet.app.data.local.SpotDao
import com.travelmeet.app.data.local.entity.SpotEntity
import com.travelmeet.app.util.Constants
import com.travelmeet.app.util.Resource
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

class SpotRepository(
    private val spotDao: SpotDao,
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage,
    private val context: Context
) {
    companion object {
        private const val TAG = "SpotRepository"
    }

    private val spotsRef = database.getReference(Constants.COLLECTION_SPOTS)
    private var realtimeListener: ValueEventListener? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun getAllSpots(): LiveData<List<SpotEntity>> = spotDao.getAllSpots()

    fun startRealtimeSync() {
        realtimeListener?.let { spotsRef.removeEventListener(it) }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUserId = auth.currentUser?.uid ?: ""
                val spots = snapshot.children.mapNotNull { child ->
                    try {
                        parseSpotSnapshot(child, currentUserId)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing spot ${child.key}: ${e.message}")
                        null
                    }
                }.sortedByDescending { it.timestamp }

                Log.d(TAG, "Realtime sync: received ${spots.size} spots from all users")
                scope.launch {
                    spotDao.deleteAll()
                    spotDao.insertSpots(spots)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Realtime sync cancelled: ${error.message}")
            }
        }

        realtimeListener = listener
        spotsRef.addValueEventListener(listener)
    }

    fun stopRealtimeSync() {
        realtimeListener?.let { spotsRef.removeEventListener(it) }
        realtimeListener = null
    }

    fun getSpotsByUser(userId: String): LiveData<List<SpotEntity>> =
        spotDao.getSpotsByUser(userId)

    fun getSpotById(spotId: String): LiveData<SpotEntity?> =
        spotDao.getSpotById(spotId)

    suspend fun syncSpots(): Resource<List<SpotEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUserId = auth.currentUser?.uid ?: ""
                Log.d(TAG, "Syncing all spots from Realtime Database...")
                val snapshot = spotsRef.get().await()

                val spots = snapshot.children.mapNotNull { child ->
                    try {
                        parseSpotSnapshot(child, currentUserId)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing spot ${child.key}: ${e.message}")
                        null
                    }
                }.sortedByDescending { it.timestamp }

                Log.d(TAG, "Synced ${spots.size} spots from all users")
                spotDao.deleteAll()
                spotDao.insertSpots(spots)
                Resource.Success(spots)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync spots: ${e.message}", e)
                Resource.Error(e.message ?: "Failed to sync spots")
            }
        }
    }

    suspend fun addSpot(
        title: String,
        description: String,
        imageUris: List<Uri>,
        latitude: Double,
        longitude: Double,
        locationName: String?
    ): Resource<SpotEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val user = auth.currentUser
                if (user == null) {
                    Log.e(TAG, "addSpot failed: user not authenticated")
                    return@withContext Resource.Error("Not authenticated")
                }
                val spotId = UUID.randomUUID().toString()
                Log.d(TAG, "Adding spot '$title' with ${imageUris.size} images for user ${user.uid}")

                // Upload images to Firebase Storage
                val imageUrls = imageUris.mapIndexed { index, uri ->
                    Log.d(TAG, "Uploading image ${index + 1}/${imageUris.size}...")
                    val mimeType = context.contentResolver.getType(uri)
                    val isGif = mimeType == "image/gif"
                    val extension = if (isGif) "gif" else "jpg"
                    val imageBytes = if (isGif) readRawBytes(uri) else compressImage(uri)
                    val imageRef = storage.reference
                        .child("${Constants.STORAGE_SPOT_IMAGES}/$spotId/${UUID.randomUUID()}.$extension")
                    imageRef.putBytes(imageBytes).await()
                    val url = imageRef.downloadUrl.await().toString()
                    Log.d(TAG, "Image ${index + 1} uploaded: $url")
                    url
                }

                val timestamp = System.currentTimeMillis()

                // Build likedBy as a map (RTDB-friendly)
                val spotData = hashMapOf<String, Any?>(
                    "id" to spotId,
                    "userId" to user.uid,
                    "username" to (user.displayName ?: "Unknown"),
                    "userPhotoUrl" to user.photoUrl?.toString(),
                    "title" to title,
                    "imageUrls" to imageUrls,
                    "description" to description,
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "locationName" to locationName,
                    "timestamp" to timestamp,
                    "likesCount" to 0,
                    "likedBy" to HashMap<String, Boolean>()
                )

                Log.d(TAG, "Writing spot to Realtime Database...")
                spotsRef.child(spotId).setValue(spotData).await()
                Log.d(TAG, "Spot '$title' saved to RTDB with id: $spotId")

                val spotEntity = SpotEntity(
                    id = spotId,
                    userId = user.uid,
                    username = user.displayName ?: "Unknown",
                    userPhotoUrl = user.photoUrl?.toString(),
                    title = title,
                    imageUrls = imageUrls,
                    description = description,
                    latitude = latitude,
                    longitude = longitude,
                    locationName = locationName,
                    timestamp = timestamp,
                    likesCount = 0,
                    isLikedByCurrentUser = false
                )

                spotDao.insertSpot(spotEntity)
                Log.d(TAG, "Spot saved to local Room DB")
                Resource.Success(spotEntity)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add spot: ${e.message}", e)
                Resource.Error(e.message ?: "Failed to add spot")
            }
        }
    }

    suspend fun updateSpot(
        spotId: String,
        title: String,
        description: String,
        newImageUris: List<Uri>,
        latitude: Double,
        longitude: Double,
        locationName: String?
    ): Resource<SpotEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val user = auth.currentUser
                if (user == null) {
                    return@withContext Resource.Error("Not authenticated")
                }
                val existingSpot = spotDao.getSpotByIdSync(spotId)
                if (existingSpot == null) {
                    return@withContext Resource.Error("Spot not found")
                }
                if (existingSpot.userId != user.uid) {
                    return@withContext Resource.Error("You can only edit your own spots")
                }

                var imageUrls = existingSpot.imageUrls

                if (newImageUris.isNotEmpty()) {
                    val newUrls = newImageUris.map { uri ->
                        val mimeType = context.contentResolver.getType(uri)
                        val isGif = mimeType == "image/gif"
                        val extension = if (isGif) "gif" else "jpg"
                        val imageBytes = if (isGif) readRawBytes(uri) else compressImage(uri)
                        val imageRef = storage.reference
                            .child("${Constants.STORAGE_SPOT_IMAGES}/$spotId/${UUID.randomUUID()}.$extension")
                        imageRef.putBytes(imageBytes).await()
                        imageRef.downloadUrl.await().toString()
                    }
                    imageUrls = imageUrls + newUrls
                }

                val updates = hashMapOf<String, Any?>(
                    "title" to title,
                    "description" to description,
                    "imageUrls" to imageUrls,
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "locationName" to locationName
                )

                spotsRef.child(spotId).updateChildren(updates).await()

                val updatedSpot = existingSpot.copy(
                    title = title,
                    description = description,
                    imageUrls = imageUrls,
                    latitude = latitude,
                    longitude = longitude,
                    locationName = locationName
                )
                spotDao.updateSpot(updatedSpot)
                Resource.Success(updatedSpot)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update spot: ${e.message}", e)
                Resource.Error(e.message ?: "Failed to update spot")
            }
        }
    }

    suspend fun deleteSpot(spotId: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val user = auth.currentUser
                if (user == null) {
                    return@withContext Resource.Error("Not authenticated")
                }
                val spot = spotDao.getSpotByIdSync(spotId)
                if (spot == null) {
                    return@withContext Resource.Error("Spot not found")
                }
                if (spot.userId != user.uid) {
                    return@withContext Resource.Error("You can only delete your own spots")
                }

                // Delete images from storage
                spot.imageUrls.forEach { url ->
                    try {
                        storage.getReferenceFromUrl(url).delete().await()
                    } catch (_: Exception) { }
                }

                spotsRef.child(spotId).removeValue().await()
                spotDao.deleteSpotById(spotId)
                Resource.Success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete spot: ${e.message}", e)
                Resource.Error(e.message ?: "Failed to delete spot")
            }
        }
    }

    suspend fun toggleLike(spotId: String): Resource<SpotEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val user = auth.currentUser
                if (user == null) {
                    return@withContext Resource.Error("Not authenticated")
                }
                val spot = spotDao.getSpotByIdSync(spotId)
                if (spot == null) {
                    return@withContext Resource.Error("Spot not found")
                }

                val spotRef = spotsRef.child(spotId)
                val snapshot = spotRef.get().await()

                // likedBy is stored as a map { "userId": true }
                val likedByMap = mutableMapOf<String, Boolean>()
                snapshot.child("likedBy").children.forEach { child ->
                    child.key?.let { key -> likedByMap[key] = true }
                }

                val isCurrentlyLiked = likedByMap.containsKey(user.uid)
                if (isCurrentlyLiked) {
                    likedByMap.remove(user.uid)
                } else {
                    likedByMap[user.uid] = true
                }

                val updates = hashMapOf<String, Any?>(
                    "likedBy" to likedByMap,
                    "likesCount" to likedByMap.size
                )
                spotRef.updateChildren(updates).await()

                val updatedSpot = spot.copy(
                    likesCount = likedByMap.size,
                    isLikedByCurrentUser = !isCurrentlyLiked
                )
                spotDao.updateSpot(updatedSpot)
                Resource.Success(updatedSpot)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to toggle like: ${e.message}", e)
                Resource.Error(e.message ?: "Failed to toggle like")
            }
        }
    }

    private fun compressImage(uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (bitmap == null) {
            Log.e(TAG, "Failed to decode image, falling back to raw bytes")
            return readRawBytes(uri)
        }

        val outputStream = ByteArrayOutputStream()
        var quality = Constants.IMAGE_QUALITY
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        while (outputStream.toByteArray().size > Constants.MAX_IMAGE_SIZE_KB * 1024 && quality > 10) {
            outputStream.reset()
            quality -= 10
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        }

        bitmap.recycle()
        return outputStream.toByteArray()
    }

    private fun readRawBytes(uri: Uri): ByteArray {
        return context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: ByteArray(0)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseSpotSnapshot(snapshot: DataSnapshot, currentUserId: String): SpotEntity {
        val imageUrls = when (val raw = snapshot.child("imageUrls").value) {
            is List<*> -> raw.filterIsInstance<String>()
            else -> {
                val single = snapshot.child("imageUrl").getValue(String::class.java)
                if (single != null) listOf(single) else emptyList()
            }
        }

        // likedBy is stored as a map { "userId": true }
        val likedByKeys = snapshot.child("likedBy").children.mapNotNull { it.key }

        return SpotEntity(
            id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: "",
            userId = snapshot.child("userId").getValue(String::class.java) ?: "",
            username = snapshot.child("username").getValue(String::class.java) ?: "",
            userPhotoUrl = snapshot.child("userPhotoUrl").getValue(String::class.java),
            title = snapshot.child("title").getValue(String::class.java) ?: "",
            imageUrls = imageUrls,
            description = snapshot.child("description").getValue(String::class.java) ?: "",
            latitude = snapshot.child("latitude").getValue(Double::class.java) ?: 0.0,
            longitude = snapshot.child("longitude").getValue(Double::class.java) ?: 0.0,
            locationName = snapshot.child("locationName").getValue(String::class.java),
            timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L,
            likesCount = snapshot.child("likesCount").getValue(Int::class.java) ?: likedByKeys.size,
            isLikedByCurrentUser = likedByKeys.contains(currentUserId)
        )
    }
}
