package com.travelmeet.app.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travelmeet.app.R

class ImageSliderAdapter(
    private val imageUrls: List<String>
) : RecyclerView.Adapter<ImageSliderAdapter.SlideViewHolder>() {

    class SlideViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_slide, parent, false) as ImageView
        return SlideViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        val url = imageUrls[position]
        if (url.isNotBlank()) {
            Glide.with(holder.imageView.context)
                .load(url)
                .placeholder(R.drawable.bg_dashed_border)
                .error(R.drawable.bg_dashed_border)
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.bg_dashed_border)
        }
    }

    override fun getItemCount(): Int = imageUrls.size
}
