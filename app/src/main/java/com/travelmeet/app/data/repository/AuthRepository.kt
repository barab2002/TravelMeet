package com.travelmeet.app.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.travelmeet.app.data.local.UserDao
import com.travelmeet.app.data.local.entity.UserEntity
import com.travelmeet.app.util.Constants
import com.travelmeet.app.util.Resource
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val userDao: UserDao
) {

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

            // Save to Firestore
            val userData = hashMapOf(
                "uid" to user.uid,
                "email" to email,
                "username" to username,
                "photoUrl" to null
            )
            firestore.collection(Constants.COLLECTION_USERS)
                .document(user.uid)
                .set(userData)
                .await()

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

            // Sync user data from Firestore to local cache
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
                ref.putFile(photoUri).await()
                photoUrl = ref.downloadUrl.await().toString()
            }

            // Update Firebase Auth profile
            val profileUpdate = UserProfileChangeRequest.Builder().apply {
                if (username != null) setDisplayName(username)
                if (photoUrl != null) setPhotoUri(Uri.parse(photoUrl))
            }.build()
            user.updateProfile(profileUpdate).await()

            // Update Firestore
            val updates = mutableMapOf<String, Any?>()
            if (username != null) updates["username"] = username
            if (photoUrl != null) updates["photoUrl"] = photoUrl

            if (updates.isNotEmpty()) {
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(user.uid)
                    .update(updates as Map<String, Any>)
                    .await()
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

    suspend fun getUserData(userId: String): Resource<UserEntity> {
        return try {
            // Try local first
            val cachedUser = userDao.getUserByIdSync(userId)
            if (cachedUser != null) return Resource.Success(cachedUser)

            // Fetch from Firestore
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
            val doc = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()

            if (doc.exists()) {
                val userEntity = UserEntity(
                    uid = doc.getString("uid") ?: userId,
                    email = doc.getString("email") ?: "",
                    username = doc.getString("username") ?: "",
                    photoUrl = doc.getString("photoUrl")
                )
                userDao.insertUser(userEntity)
            }
        } catch (_: Exception) {
            // Silently fail - cached data will be used
        }
    }
}
