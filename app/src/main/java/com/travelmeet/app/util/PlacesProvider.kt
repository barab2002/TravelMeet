package com.travelmeet.app.util

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.travelmeet.app.R

/**
 * Provides a singleton [PlacesClient] so we don't spin up a new gRPC channel for every fragment.
 */
object PlacesProvider {
    @Volatile
    private var placesClient: PlacesClient? = null

    fun getClient(context: Context): PlacesClient {
        var client = placesClient
        if (client == null) {
            synchronized(this) {
                client = placesClient
                if (client == null) {
                    val appContext = context.applicationContext
                    if (!Places.isInitialized()) {
                        Places.initialize(appContext, appContext.getString(R.string.google_maps_key))
                    }
                    client = Places.createClient(appContext).also { placesClient = it }
                }
            }
        }
        return client!!
    }
}

