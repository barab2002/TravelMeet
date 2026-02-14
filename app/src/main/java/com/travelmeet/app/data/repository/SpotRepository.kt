package com.travelmeet.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val context: Context
) {
    companion object {
        private const val TAG = "SpotRepository"
    }

    private var snapshotListener: ListenerRegistration? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun getAllSpots(): LiveData<List<SpotEntity>> = spotDao.getAllSpots()

    fun startRealtimeSync() {
        snapshotListener?.remove()
        snapshotListener = firestore.collection(Constants.COLLECTION_SPOTS)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Realtime sync error: ${error.message}", error)
                    return@addSnapshotListener
                }
                if (snapshot == null) return@addSnapshotListener

                val currentUserId = auth.currentUser?.uid ?: ""
                val spots = snapshot.documents.mapNotNull { doc ->
                    try {
                        val likedBy = doc.get("likedBy") as? List<*> ?: emptyList<String>()
                        val imageUrls = doc.get("imageUrls") as? List<String> ?: doc.getString("imageUrl")?.let { listOf(it) } ?: emptyList()
                        SpotEntity(
                            id = doc.getString("id") ?: doc.id,
                            userId = doc.getString("userId") ?: "",
                            username = doc.getString("username") ?: "",
                            userPhotoUrl = doc.getString("userPhotoUrl"),
                            title = doc.getString("title") ?: "",
                            imageUrls = imageUrls,
                            description = doc.getString("description") ?: "",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0,
                            locationName = doc.getString("locationName"),
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            likesCount = doc.getLong("likesCount")?.toInt() ?: 0,
                            isLikedByCurrentUser = likedBy.contains(currentUserId)
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing spot doc ${doc.id}: ${e.message}")
                        null
                    }
                }
                Log.d(TAG, "Realtime sync: received ${spots.size} spots from all users")
                scope.launch {
                    spotDao.deleteAll()
                    spotDao.insertSpots(spots)
                }
            }
    }

    fun stopRealtimeSync() {
        snapshotListener?.remove()
        snapshotListener = null
    }

    fun getSpotsByUser(userId: String): LiveData<List<SpotEntity>> =
        spotDao.getSpotsByUser(userId)

    fun getSpotById(spotId: String): LiveData<SpotEntity?> =
        spotDao.getSpotById(spotId)

    suspend fun syncSpots(): Resource<List<SpotEntity>> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: ""
            Log.d(TAG, "Syncing all spots from Firestore...")
            val snapshot = firestore.collection(Constants.COLLECTION_SPOTS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val spots = snapshot.documents.mapNotNull { doc ->
                try {
                    val likedBy = doc.get("likedBy") as? List<*> ?: emptyList<String>()
                    val imageUrls = doc.get("imageUrls") as? List<String> ?: doc.getString("imageUrl")?.let { listOf(it) } ?: emptyList()
                    SpotEntity(
                        id = doc.getString("id") ?: doc.id,
                        userId = doc.getString("userId") ?: "",
                        username = doc.getString("username") ?: "",
                        userPhotoUrl = doc.getString("userPhotoUrl"),
                        title = doc.getString("title") ?: "",
                        imageUrls = imageUrls,
                        description = doc.getString("description") ?: "",
                        latitude = doc.getDouble("latitude") ?: 0.0,
                        longitude = doc.getDouble("longitude") ?: 0.0,
                        locationName = doc.getString("locationName"),
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        likesCount = doc.getLong("likesCount")?.toInt() ?: 0,
                        isLikedByCurrentUser = likedBy.contains(currentUserId)
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing spot doc ${doc.id}: ${e.message}")
                    null
                }
            }

            Log.d(TAG, "Synced ${spots.size} spots from all users")
            spotDao.deleteAll()
            spotDao.insertSpots(spots)
            Resource.Success(spots)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync spots: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to sync spots")
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
        return try {
            val user = auth.currentUser ?: return Resource.Error("Not authenticated")
            val spotId = UUID.randomUUID().toString()
            Log.d(TAG, "Adding spot '$title' with ${imageUris.size} images for user ${user.uid}")

            val imageUrls = imageUris.map { uri ->
                val mimeType = context.contentResolver.getType(uri)
                val isGif = mimeType == "image/gif"
                val extension = if (isGif) "gif" else "jpg"
                val imageBytes = if (isGif) readRawBytes(uri) else compressImage(uri)
                val imageRef = storage.reference
                    .child("${Constants.STORAGE_SPOT_IMAGES}/$spotId/${UUID.randomUUID()}.$extension")
                imageRef.putBytes(imageBytes).await()
                imageRef.downloadUrl.await().toString()
            }

            val timestamp = System.currentTimeMillis()
            val spotData = hashMapOf(
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
                "likedBy" to emptyList<String>()
            )

            firestore.collection(Constants.COLLECTION_SPOTS)
                .document(spotId)
                .set(spotData)
                .await()
            Log.d(TAG, "Spot '$title' saved to Firestore with id: $spotId")

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

    suspend fun updateSpot(
        spotId: String,
        title: String,
        description: String,
        newImageUris: List<Uri>,
        latitude: Double,
        longitude: Double,
        locationName: String?
    ): Resource<SpotEntity> {
        return try {
            val user = auth.currentUser ?: return Resource.Error("Not authenticated")
            val existingSpot = spotDao.getSpotByIdSync(spotId)
                ?: return Resource.Error("Spot not found")

            if (existingSpot.userId != user.uid) {
                return Resource.Error("You can only edit your own spots")
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

            val updates = mapOf(
                "title" to title,
                "description" to description,
                "imageUrls" to imageUrls,
                "latitude" to latitude,
                "longitude" to longitude,
                "locationName" to locationName
            )

            firestore.collection(Constants.COLLECTION_SPOTS)
                .document(spotId)
                .update(updates)
                .await()

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
            Resource.Error(e.message ?: "Failed to update spot")
        }
    }

    suspend fun deleteSpot(spotId: String): Resource<Unit> {
        return try {
            val user = auth.currentUser ?: return Resource.Error("Not authenticated")
            val spot = spotDao.getSpotByIdSync(spotId)
                ?: return Resource.Error("Spot not found")

            if (spot.userId != user.uid) {
                return Resource.Error("You can only delete your own spots")
            }

            // Delete images from storage
            spot.imageUrls.forEach { url ->
                try {
                    storage.getReferenceFromUrl(url).delete().await()
                } catch (_: Exception) {
                    // Image may not exist, continue
                }
            }

            firestore.collection(Constants.COLLECTION_SPOTS)
                .document(spotId)
                .delete()
                .await()

            spotDao.deleteSpotById(spotId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete spot")
        }
    }

    suspend fun toggleLike(spotId: String): Resource<SpotEntity> {
        return try {
            val user = auth.currentUser ?: return Resource.Error("Not authenticated")
            val spot = spotDao.getSpotByIdSync(spotId)
                ?: return Resource.Error("Spot not found")

            val spotRef = firestore.collection(Constants.COLLECTION_SPOTS).document(spotId)
            val doc = spotRef.get().await()
            val likedBy = (doc.get("likedBy") as? List<*>)?.toMutableList() ?: mutableListOf()

            val isCurrentlyLiked = likedBy.contains(user.uid)
            if (isCurrentlyLiked) {
                likedBy.remove(user.uid)
            } else {
                likedBy.add(user.uid)
            }

            spotRef.update(
                mapOf(
                    "likedBy" to likedBy,
                    "likesCount" to likedBy.size
                )
            ).await()

            val updatedSpot = spot.copy(
                likesCount = likedBy.size,
                isLikedByCurrentUser = !isCurrentlyLiked
            )
            spotDao.updateSpot(updatedSpot)
            Resource.Success(updatedSpot)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to toggle like")
        }
    }

    private fun compressImage(uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val outputStream = ByteArrayOutputStream()
        var quality = Constants.IMAGE_QUALITY
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        // Compress until under max size
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
}
