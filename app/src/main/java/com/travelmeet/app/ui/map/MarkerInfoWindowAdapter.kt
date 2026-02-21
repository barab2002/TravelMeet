package com.travelmeet.app.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.travelmeet.app.R
import com.travelmeet.app.data.local.entity.SpotEntity
import android.util.LruCache

class MarkerInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private val imageCache = object : LruCache<String, Bitmap>(CACHE_SIZE_BYTES) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
    }

    private val inflightTargets = mutableMapOf<String, Target>()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun getInfoContents(marker: Marker): View? {
        val spot = marker.tag as? SpotEntity ?: return null

        val view = LayoutInflater.from(context).inflate(R.layout.item_marker_info, null)

        val titleTextView = view.findViewById<TextView>(R.id.tv_spot_title)
        val userTextView = view.findViewById<TextView>(R.id.tv_spot_user)
        val imageView = view.findViewById<ImageView>(R.id.iv_spot_image)
        val progressBar = view.findViewById<ProgressBar>(R.id.pb_image_loading)

        val resolvedTitle = spot.title.takeIf { it.isNotBlank() } ?: marker.title
        titleTextView.text = resolvedTitle ?: context.getString(R.string.map_unknown_spot)
        titleTextView.visibility = View.VISIBLE
        userTextView.text = "Created by: ${spot.username}"

        when (val previewUrl = spot.imageUrls.firstOrNull { it.isNotBlank() }) {
            null -> {
                imageView.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
            else -> {
                val cached = imageCache.get(previewUrl)
                if (cached != null) {
                    imageView.setImageBitmap(cached)
                    imageView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                } else {
                    imageView.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE
                    val targetKey = previewUrl
                    if (!inflightTargets.containsKey(targetKey)) {
                        val target = object : Target {
                            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                bitmap?.let { imageCache.put(previewUrl, it) }
                                inflightTargets.remove(targetKey)
                                refreshInfoWindow(marker)
                            }

                            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                                inflightTargets.remove(targetKey)
                                refreshInfoWindow(marker)
                            }

                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                        }
                        inflightTargets[targetKey] = target
                        Picasso.get()
                            .load(previewUrl)
                            .config(Bitmap.Config.RGB_565)
                            .resize(PREVIEW_SIZE_PX, PREVIEW_SIZE_PX)
                            .centerCrop()
                            .noFade()
                            .into(target)
                     }
                 }
             }
         }

        return view
     }

    override fun getInfoWindow(marker: Marker): View? {
        // We are using a custom info window layout, so we return null here
        return null
    }

    private companion object {
        private const val CACHE_SIZE_BYTES = 4 * 1024 * 1024 // 4MB
        private const val PREVIEW_SIZE_PX = 256
    }

    private fun refreshInfoWindow(marker: Marker) {
        if (!marker.isInfoWindowShown) return
        mainHandler.post {
            if (!marker.isInfoWindowShown) return@post
            marker.hideInfoWindow()
            marker.showInfoWindow()
        }
    }
}
