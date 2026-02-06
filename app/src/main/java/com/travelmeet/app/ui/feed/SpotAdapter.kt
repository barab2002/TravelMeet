package com.travelmeet.app.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.travelmeet.app.R
import com.travelmeet.app.data.local.entity.SpotEntity
import com.travelmeet.app.databinding.ItemSpotBinding
import com.travelmeet.app.util.TimeUtils

class SpotAdapter(
    private val onItemClick: (SpotEntity) -> Unit
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

            // Load spot image
            if (spot.imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(spot.imageUrl)
                    .placeholder(R.drawable.bg_dashed_border)
                    .error(R.drawable.bg_dashed_border)
                    .into(binding.ivSpotImage)
            }

            // Load user avatar
            if (!spot.userPhotoUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(spot.userPhotoUrl)
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.ivUserAvatar)
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
