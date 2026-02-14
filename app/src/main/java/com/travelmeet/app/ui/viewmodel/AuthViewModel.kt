package com.travelmeet.app.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.travelmeet.app.data.local.AppDatabase
import com.travelmeet.app.data.local.entity.UserEntity
import com.travelmeet.app.data.repository.AuthRepository
import com.travelmeet.app.util.Resource
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AuthRepository

    private val _authState = MutableLiveData<Resource<FirebaseUser>?>()
    val authState: LiveData<Resource<FirebaseUser>?> = _authState

    private val _profileUpdateState = MutableLiveData<Resource<FirebaseUser>>()
    val profileUpdateState: LiveData<Resource<FirebaseUser>> = _profileUpdateState

    private val _userData = MutableLiveData<Resource<UserEntity>>()
    val userData: LiveData<Resource<UserEntity>> = _userData

    val currentUser: FirebaseUser? get() = repository.currentUser
    val currentUserId: String? get() = repository.currentUser?.uid

    init {
        val db = AppDatabase.getInstance(application)
        val rtdb = FirebaseDatabase.getInstance("https://travelmeet-9602b-default-rtdb.firebaseio.com/")
        repository = AuthRepository(
            FirebaseAuth.getInstance(),
            rtdb,
            FirebaseStorage.getInstance(),
            db.userDao()
        )
    }

    fun register(email: String, password: String, username: String) {
        _authState.value = Resource.Loading()
        viewModelScope.launch {
            _authState.value = repository.register(email, password, username)
        }
    }

    fun login(email: String, password: String) {
        _authState.value = Resource.Loading()
        viewModelScope.launch {
            _authState.value = repository.login(email, password)
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = null
    }

    fun clearAuthState() {
        _authState.value = null
    }

    fun updateProfile(username: String?, photoUri: Uri?) {
        _profileUpdateState.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.updateProfile(username, photoUri)

            // Clear image caches after successful update
            if (result is Resource.Success && photoUri != null) {
                // Clear Picasso cache
                Picasso.get().invalidate(result.data?.photoUrl)

                // Clear Glide cache
                try {
                    val context = getApplication<Application>().applicationContext
                    Glide.get(context).clearMemory()
                    // Note: clearDiskCache must run on background thread
                    Thread {
                        Glide.get(context).clearDiskCache()
                    }.start()
                } catch (e: Exception) {
                    // Silently fail - cache clearing is not critical
                }
            }

            _profileUpdateState.postValue(result)
        }
    }

    fun loadUserData(userId: String) {
        _userData.value = Resource.Loading()
        viewModelScope.launch {
            _userData.value = repository.getUserData(userId)
        }
    }
}
