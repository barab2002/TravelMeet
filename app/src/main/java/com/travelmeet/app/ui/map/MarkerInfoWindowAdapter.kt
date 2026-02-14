package com.travelmeet.app.ui.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import com.travelmeet.app.R
import com.travelmeet.app.data.local.entity.SpotEntity

class MarkerInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker): View? {
        val spot = marker.tag as? SpotEntity ?: return null

        val view = LayoutInflater.from(context).inflate(R.layout.item_marker_info, null)

        val titleTextView = view.findViewById<TextView>(R.id.tv_spot_title)
        val userTextView = view.findViewById<TextView>(R.id.tv_spot_user)
        val imageView = view.findViewById<ImageView>(R.id.iv_spot_image)

        titleTextView.text = spot.title
        userTextView.text = "Created by: ${spot.username}"

        if (spot.imageUrls.isNotEmpty()) {
            Picasso.get()
                .load(spot.imageUrls.first())
                .into(imageView)
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
        }

        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        // We are using a custom info window layout, so we return null here
        return null
    }
}
