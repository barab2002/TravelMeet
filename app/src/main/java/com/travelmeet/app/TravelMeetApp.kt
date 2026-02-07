package com.travelmeet.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.libraries.places.api.Places

class TravelMeetApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Apply saved theme preference
        val prefs = getSharedPreferences("settings", 0)
        val isDark = prefs.getBoolean("dark_mode", true)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        if (!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.MAPS_API_KEY)
        }
    }

    companion object {
        lateinit var instance: TravelMeetApp
            private set
    }
}
