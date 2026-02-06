package com.travelmeet.app.data.repository

import com.travelmeet.app.data.remote.WeatherApiService
import com.travelmeet.app.data.remote.model.WeatherResponse
import com.travelmeet.app.util.Constants
import com.travelmeet.app.util.Resource

class WeatherRepository(
    private val weatherApiService: WeatherApiService
) {

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Resource<WeatherResponse> {
        return try {
            val response = weatherApiService.getCurrentWeather(
                latitude = latitude,
                longitude = longitude,
                apiKey = Constants.WEATHER_API_KEY
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error("Weather API error: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch weather")
        }
    }
}
