package com.travelmeet.app

import android.app.Application
import com.google.android.libraries.places.api.Places

class TravelMeetApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.MAPS_API_KEY)
        }
    }

    companion object {
        lateinit var instance: TravelMeetApp
            private set
    }
}
