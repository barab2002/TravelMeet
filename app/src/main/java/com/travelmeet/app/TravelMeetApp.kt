package com.travelmeet.app

import android.app.Application

class TravelMeetApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TravelMeetApp
            private set
    }
}
