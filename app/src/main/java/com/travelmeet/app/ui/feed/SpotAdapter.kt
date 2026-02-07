package com.travelmeet.app.ui.feed

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.Picasso
import com.travelmeet.app.R
import com.travelmeet.app.data.local.entity.SpotEntity
import com.travelmeet.app.databinding.ItemSpotBinding
import com.travelmeet.app.util.TimeUtils

class SpotAdapter(
    private val onItemClick: (SpotEntity) -> Unit,
    private val onLikeClick: (SpotEntity) -> Unit
) : ListAdapter<SpotEntity, SpotAdapter.SpotViewHolder>(SpotDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotViewHolder {
        val binding = ItemSpotBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SpotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SpotViewHolder(
        private val binding: ItemSpotBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
            binding.ivLike.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onLikeClick(getItem(position))
                }
            }
        }

        fun bind(spot: SpotEntity) {
            binding.tvTitle.text = spot.title
            binding.tvDescription.text = spot.description
            binding.tvUsername.text = spot.username
            binding.tvTimestamp.text = TimeUtils.getRelativeTimeString(spot.timestamp)
            binding.tvLikes.text = spot.likesCount.toString()
            binding.tvLocation.text = spot.locationName ?: String.format(
                "%.4f, %.4f", spot.latitude, spot.longitude
            )

            // Set up image carousel
            val urls = spot.imageUrls.filter { it.isNotBlank() }
            if (urls.isNotEmpty()) {
                binding.vpImages.adapter = ImageSliderAdapter(urls)
            } else {
                binding.vpImages.adapter = ImageSliderAdapter(emptyList())
            }

            // Show dot indicator and count only for multi-image posts
            if (urls.size > 1) {
                binding.tvImageCount.visibility = View.VISIBLE
                binding.tvImageCount.text = "1/${urls.size}"
                binding.dotIndicator.visibility = View.VISIBLE
                setupDots(binding.dotIndicator, urls.size, 0)

                binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        binding.tvImageCount.text = "${position + 1}/${urls.size}"
                        updateDots(binding.dotIndicator, position)
                    }
                })
            } else {
                binding.tvImageCount.visibility = View.GONE
                binding.dotIndicator.visibility = View.GONE
            }

            // Load user avatar
            if (!spot.userPhotoUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(spot.userPhotoUrl)
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.ivUserAvatar)
            }

            val likeIcon = if (spot.isLikedByCurrentUser) R.drawable.ic_like_filled else R.drawable.ic_like_outline
            binding.ivLike.setImageResource(likeIcon)
        }

        private fun setupDots(container: LinearLayout, count: Int, activeIndex: Int) {
            container.removeAllViews()
            for (i in 0 until count) {
                val dot = View(container.context)
                val size = if (i == activeIndex) 8 else 6
                val dp = (size * container.context.resources.displayMetrics.density).toInt()
                val params = LinearLayout.LayoutParams(dp, dp)
                params.setMargins(4, 0, 4, 0)
                dot.layoutParams = params
                val drawable = GradientDrawable()
                drawable.shape = GradientDrawable.OVAL
                drawable.setColor(
                    if (i == activeIndex) 0xFFFFFFFF.toInt() else 0x80FFFFFF.toInt()
                )
                dot.background = drawable
                container.addView(dot)
            }
        }

        private fun updateDots(container: LinearLayout, activeIndex: Int) {
            for (i in 0 until container.childCount) {
                val dot = container.getChildAt(i)
                val isActive = i == activeIndex
                val size = if (isActive) 8 else 6
                val dp = (size * container.context.resources.displayMetrics.density).toInt()
                val params = dot.layoutParams as LinearLayout.LayoutParams
                params.width = dp
                params.height = dp
                dot.layoutParams = params
                val drawable = GradientDrawable()
                drawable.shape = GradientDrawable.OVAL
                drawable.setColor(
                    if (isActive) 0xFFFFFFFF.toInt() else 0x80FFFFFF.toInt()
                )
                dot.background = drawable
            }
        }
    }

    class SpotDiffCallback : DiffUtil.ItemCallback<SpotEntity>() {
        override fun areItemsTheSame(oldItem: SpotEntity, newItem: SpotEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SpotEntity, newItem: SpotEntity): Boolean =
            oldItem == newItem
    }
}
