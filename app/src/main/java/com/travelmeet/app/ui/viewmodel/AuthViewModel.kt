package com.travelmeet.app.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.travelmeet.app.data.local.AppDatabase
import com.travelmeet.app.data.local.entity.UserEntity
import com.travelmeet.app.data.repository.AuthRepository
import com.travelmeet.app.util.Resource
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AuthRepository

    private val _authState = MutableLiveData<Resource<FirebaseUser>>()
    val authState: LiveData<Resource<FirebaseUser>> = _authState

    private val _profileUpdateState = MutableLiveData<Resource<FirebaseUser>>()
    val profileUpdateState: LiveData<Resource<FirebaseUser>> = _profileUpdateState

    private val _userData = MutableLiveData<Resource<UserEntity>>()
    val userData: LiveData<Resource<UserEntity>> = _userData

    val currentUser: FirebaseUser? get() = repository.currentUser
    val currentUserId: String? get() = repository.currentUser?.uid

    init {
        val db = AppDatabase.getInstance(application)
        repository = AuthRepository(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance(),
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
    }

    fun updateProfile(username: String?, photoUri: Uri?) {
        _profileUpdateState.value = Resource.Loading()
        viewModelScope.launch {
            _profileUpdateState.value = repository.updateProfile(username, photoUri)
        }
    }

    fun loadUserData(userId: String) {
        _userData.value = Resource.Loading()
        viewModelScope.launch {
            _userData.value = repository.getUserData(userId)
        }
    }
}
