package com.travelmeet.app.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.travelmeet.app.data.local.UserDao
import com.travelmeet.app.data.local.entity.UserEntity
import com.travelmeet.app.util.Constants
import com.travelmeet.app.util.Resource
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage,
    private val userDao: UserDao
) {

    private val usersRef = database.getReference(Constants.COLLECTION_USERS)

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun register(
        email: String,
        password: String,
        username: String
    ): Resource<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Resource.Error("Registration failed")

            // Update display name
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            user.updateProfile(profileUpdate).await()

            // Save to Realtime Database
            val userData = hashMapOf<String, Any?>(
                "uid" to user.uid,
                "email" to email,
                "username" to username,
                "photoUrl" to null
            )
            usersRef.child(user.uid).setValue(userData).await()

            // Cache locally
            userDao.insertUser(
                UserEntity(
                    uid = user.uid,
                    email = email,
                    username = username,
                    photoUrl = null
                )
            )

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Resource.Error("Login failed")

            // Sync user data from RTDB to local cache
            syncUserData(user.uid)

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun updateProfile(
        username: String?,
        photoUri: Uri?
    ): Resource<FirebaseUser> {
        return try {
            val user = auth.currentUser ?: return Resource.Error("Not authenticated")
            var photoUrl: String? = null

            // Upload photo if provided
            if (photoUri != null) {
                val ref = storage.reference
                    .child("${Constants.STORAGE_PROFILE_IMAGES}/${user.uid}.jpg")
                // Add cache-busting parameter to force new URL on every upload
                ref.putFile(photoUri).await()
                photoUrl = ref.downloadUrl.await().toString()
                    .let { url ->
                        // Add timestamp parameter to bypass Picasso/Glide cache
                        if (url.contains("?")) "$url&t=${System.currentTimeMillis()}"
                        else "$url?t=${System.currentTimeMillis()}"
                    }
            }

            // Update Firebase Auth profile
            val profileUpdate = UserProfileChangeRequest.Builder().apply {
                if (username != null) setDisplayName(username)
                if (photoUrl != null) setPhotoUri(Uri.parse(photoUrl))
            }.build()
            user.updateProfile(profileUpdate).await()

            // Update Realtime Database
            val updates = hashMapOf<String, Any?>()
            if (username != null) updates["username"] = username
            if (photoUrl != null) {
                updates["photoUrl"] = photoUrl
                // Also update all user's spots with new profile photo
                updateUserSpotsPhotoUrl(user.uid, photoUrl)
            }

            if (updates.isNotEmpty()) {
                usersRef.child(user.uid).updateChildren(updates).await()
            }

            // Update local cache
            val cachedUser = userDao.getUserByIdSync(user.uid)
            if (cachedUser != null) {
                userDao.insertUser(
                    cachedUser.copy(
                        username = username ?: cachedUser.username,
                        photoUrl = photoUrl ?: cachedUser.photoUrl
                    )
                )
            }

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Profile update failed")
        }
    }

    private suspend fun updateUserSpotsPhotoUrl(userId: String, newPhotoUrl: String) {
        try {
            // Update all spots created by this user with new photo URL
            val spotsRef = database.getReference(Constants.COLLECTION_SPOTS)
            val snapshot = spotsRef.get().await()

            snapshot.children.forEach { spotSnapshot ->
                val spotUserId = spotSnapshot.child("userId").getValue(String::class.java)
                if (spotUserId == userId) {
                    spotSnapshot.key?.let { spotId ->
                        spotsRef.child(spotId).child("userPhotoUrl").setValue(newPhotoUrl).await()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to update spots photo URL: ${e.message}")
        }
    }

    suspend fun getUserData(userId: String): Resource<UserEntity> {
        return try {
            // Try local first
            val cachedUser = userDao.getUserByIdSync(userId)
            if (cachedUser != null) return Resource.Success(cachedUser)

            // Fetch from Realtime Database
            syncUserData(userId)
            val user = userDao.getUserByIdSync(userId)
                ?: return Resource.Error("User not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get user data")
        }
    }

    private suspend fun syncUserData(userId: String) {
        try {
            val snapshot = usersRef.child(userId).get().await()

            if (snapshot.exists()) {
                val userEntity = UserEntity(
                    uid = snapshot.child("uid").getValue(String::class.java) ?: userId,
                    email = snapshot.child("email").getValue(String::class.java) ?: "",
                    username = snapshot.child("username").getValue(String::class.java) ?: "",
                    photoUrl = snapshot.child("photoUrl").getValue(String::class.java)
                )
                userDao.insertUser(userEntity)
            }
        } catch (_: Exception) {
            // Silently fail - cached data will be used
        }
    }
}
