package com.travelmeet.app.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.travelmeet.app.data.local.AppDatabase
import com.travelmeet.app.data.local.entity.SpotEntity
import com.travelmeet.app.data.repository.SpotRepository
import com.travelmeet.app.util.Resource
import kotlinx.coroutines.launch

class SpotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SpotRepository
    val allSpots: LiveData<List<SpotEntity>>

    private val _addSpotState = MutableLiveData<Resource<SpotEntity>>()
    val addSpotState: LiveData<Resource<SpotEntity>> = _addSpotState

    private val _updateSpotState = MutableLiveData<Resource<SpotEntity>>()
    val updateSpotState: LiveData<Resource<SpotEntity>> = _updateSpotState

    private val _deleteSpotState = MutableLiveData<Resource<Unit>>()
    val deleteSpotState: LiveData<Resource<Unit>> = _deleteSpotState

    private val _syncState = MutableLiveData<Resource<List<SpotEntity>>>()
    val syncState: LiveData<Resource<List<SpotEntity>>> = _syncState

    private val _likeState = MutableLiveData<Resource<SpotEntity>>()
    val likeState: LiveData<Resource<SpotEntity>> = _likeState

    init {
        val db = AppDatabase.getInstance(application)
        repository = SpotRepository(
            db.spotDao(),
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance(),
            application.applicationContext
        )
        allSpots = repository.getAllSpots()
        repository.startRealtimeSync()
    }

    fun syncSpots() {
        _syncState.value = Resource.Loading()
        viewModelScope.launch {
            _syncState.value = repository.syncSpots()
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopRealtimeSync()
    }

    fun getSpotsByUser(userId: String): LiveData<List<SpotEntity>> =
        repository.getSpotsByUser(userId)

    fun getSpotById(spotId: String): LiveData<SpotEntity?> =
        repository.getSpotById(spotId)

    fun addSpot(
        title: String,
        description: String,
        imageUris: List<Uri>,
        latitude: Double,
        longitude: Double,
        locationName: String?
    ) {
        _addSpotState.value = Resource.Loading()
        viewModelScope.launch {
            _addSpotState.value = repository.addSpot(
                title, description, imageUris, latitude, longitude, locationName
            )
        }
    }

    fun updateSpot(
        spotId: String,
        title: String,
        description: String,
        newImageUris: List<Uri>,
        latitude: Double,
        longitude: Double,
        locationName: String?
    ) {
        _updateSpotState.value = Resource.Loading()
        viewModelScope.launch {
            _updateSpotState.value = repository.updateSpot(
                spotId, title, description, newImageUris, latitude, longitude, locationName
            )
        }
    }

    fun deleteSpot(spotId: String) {
        _deleteSpotState.value = Resource.Loading()
        viewModelScope.launch {
            _deleteSpotState.value = repository.deleteSpot(spotId)
        }
    }

    fun toggleLike(spotId: String) {
        viewModelScope.launch {
            _likeState.value = repository.toggleLike(spotId)
        }
    }
}
