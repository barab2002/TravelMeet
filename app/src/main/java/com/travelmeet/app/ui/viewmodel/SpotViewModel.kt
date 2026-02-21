package com.travelmeet.app.ui.viewmodel

import android.app.Application
import android.location.Location
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.travelmeet.app.data.local.AppDatabase
import com.travelmeet.app.data.local.entity.SpotEntity
import com.travelmeet.app.data.repository.SpotRepository
import com.travelmeet.app.ui.feed.SpotSortOption
import com.travelmeet.app.ui.feed.sort
import com.travelmeet.app.util.Resource
import kotlinx.coroutines.launch

class SpotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SpotRepository
    val allSpots: LiveData<List<SpotEntity>>
    private val _feedSpots = MediatorLiveData<List<SpotEntity>>()
    val feedSpots: LiveData<List<SpotEntity>> = _feedSpots

    private val _sortOption = MutableLiveData(SpotSortOption.DEFAULT)
    val sortOption: LiveData<SpotSortOption> = _sortOption
    private var latestSpots: List<SpotEntity> = emptyList()

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

    private var referenceLatitude: Double? = null
    private var referenceLongitude: Double? = null
    private var maxDistanceMeters: Double? = null
    private var lastLocationName: String? = null
    private var lastDistanceRaw: String? = null
    private var lastDistanceUnit: String? = null
    private var searchQuery: String? = null

    init {
        val db = AppDatabase.getInstance(application)
        val rtdb = FirebaseDatabase.getInstance("https://travelmeet-9602b-default-rtdb.firebaseio.com/")
        repository = SpotRepository(
            db.spotDao(),
            FirebaseAuth.getInstance(),
            rtdb,
            FirebaseStorage.getInstance(),
            application.applicationContext
        )
        allSpots = repository.getAllSpots()
        repository.startRealtimeSync()

        _feedSpots.addSource(allSpots) { spots ->
            latestSpots = spots
            val option = _sortOption.value ?: SpotSortOption.DEFAULT
            _feedSpots.value = applySorting(option, latestSpots)
        }
        _feedSpots.addSource(_sortOption) { option ->
            _feedSpots.value = applySorting(option, latestSpots)
        }
    }

    private fun applySorting(option: SpotSortOption, spots: List<SpotEntity>): List<SpotEntity> {
        val refLat = referenceLatitude
        val refLng = referenceLongitude
        val maxDist = maxDistanceMeters
        val query = searchQuery?.lowercase()

        // 1) Text filter (Search spots)
        val filteredByText = if (query != null) {
            spots.filter { spot ->
                val title = spot.title.lowercase()
                val desc = spot.description.lowercase()
                val loc = spot.locationName?.lowercase() ?: ""
                title.contains(query) || desc.contains(query) || loc.contains(query)
            }
        } else {
            spots
        }

        // 2) Distance filter + sort
        val base = if (refLat != null && refLng != null) {
            filteredByText
                .map { spot ->
                    val distance = distanceBetweenMeters(refLat, refLng, spot.latitude, spot.longitude).toDouble()
                    spot to distance
                }
                .let { listWithDistance ->
                    val filtered = if (maxDist != null && maxDist > 0) {
                        listWithDistance.filter { it.second <= maxDist }
                    } else listWithDistance
                    filtered.sortedBy { it.second }.map { it.first }
                }
        } else {
            option.sort(filteredByText)
        }

        return base
    }

    private fun distanceBetweenMeters(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Float {
        val result = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, result)
        return result[0]
    }

    fun syncSpots() {
        _syncState.value = Resource.Loading()
        viewModelScope.launch {
            try {
                _syncState.postValue(repository.syncSpots())
            } catch (e: Exception) {
                _syncState.postValue(Resource.Error(e.message ?: "Sync failed"))
            }
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
            try {
                val result = repository.addSpot(
                    title, description, imageUris, latitude, longitude, locationName
                )
                _addSpotState.postValue(result)
            } catch (e: Exception) {
                _addSpotState.postValue(Resource.Error(e.message ?: "Failed to add spot"))
            }
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
            try {
                val result = repository.updateSpot(
                    spotId, title, description, newImageUris, latitude, longitude, locationName
                )
                _updateSpotState.postValue(result)
            } catch (e: Exception) {
                _updateSpotState.postValue(Resource.Error(e.message ?: "Failed to update spot"))
            }
        }
    }

    fun deleteSpot(spotId: String) {
        _deleteSpotState.value = Resource.Loading()
        viewModelScope.launch {
            try {
                _deleteSpotState.postValue(repository.deleteSpot(spotId))
            } catch (e: Exception) {
                _deleteSpotState.postValue(Resource.Error(e.message ?: "Failed to delete spot"))
            }
        }
    }

    fun toggleLike(spotId: String) {
        viewModelScope.launch {
            try {
                _likeState.postValue(repository.toggleLike(spotId))
            } catch (e: Exception) {
                _likeState.postValue(Resource.Error(e.message ?: "Failed to toggle like"))
            }
        }
    }

    fun setSortOption(option: SpotSortOption) {
        if (_sortOption.value != option) {
            _sortOption.value = option
        }
    }

    fun getLastLocationName(): String? = lastLocationName
    fun getLastDistanceRaw(): String? = lastDistanceRaw
    fun getLastDistanceUnit(): String? = lastDistanceUnit

    fun setFilters(
        latitude: Double?,
        longitude: Double?,
        maxDistanceMeters: Double?,
        locationName: String?,
        rawDistance: String?,
        distanceUnit: String?
    ) {
        referenceLatitude = latitude
        referenceLongitude = longitude
        this.maxDistanceMeters = maxDistanceMeters
        lastLocationName = locationName
        lastDistanceRaw = rawDistance
        lastDistanceUnit = distanceUnit
        val option = _sortOption.value ?: SpotSortOption.DEFAULT
        _feedSpots.value = applySorting(option, latestSpots)
    }

    fun setReferenceLocation(latitude: Double?, longitude: Double?, maxDistanceMeters: Double? = null) {
        // Keep for backwards compatibility where only distance + ref location are set
        setFilters(latitude, longitude, maxDistanceMeters, lastLocationName, lastDistanceRaw, lastDistanceUnit)
    }

    fun getSearchQuery(): String? = searchQuery

    fun setSearchQuery(query: String?) {
        searchQuery = query?.takeIf { it.isNotBlank() }
        val option = _sortOption.value ?: SpotSortOption.DEFAULT
        _feedSpots.value = applySorting(option, latestSpots)
    }
}
