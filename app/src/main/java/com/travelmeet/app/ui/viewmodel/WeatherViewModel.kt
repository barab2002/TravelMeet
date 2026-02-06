package com.travelmeet.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelmeet.app.data.remote.WeatherApiService
import com.travelmeet.app.data.remote.model.WeatherResponse
import com.travelmeet.app.data.repository.WeatherRepository
import com.travelmeet.app.util.Resource
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository(WeatherApiService.create())

    private val _weatherData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherData: LiveData<Resource<WeatherResponse>> = _weatherData

    fun fetchWeather(latitude: Double, longitude: Double) {
        _weatherData.value = Resource.Loading()
        viewModelScope.launch {
            _weatherData.value = repository.getCurrentWeather(latitude, longitude)
        }
    }
}
