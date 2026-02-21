package com.travelmeet.app.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.travelmeet.app.R
import com.travelmeet.app.data.model.SpotComment
import com.travelmeet.app.databinding.ItemCommentBinding

class CommentsAdapter : ListAdapter<SpotComment, CommentsAdapter.CommentViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: SpotComment) {
            binding.tvCommentUsername.text = comment.username
            binding.tvCommentText.text = comment.text
            binding.tvCommentTimestamp.text = comment.relativeTime
            if (!comment.userPhotoUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(comment.userPhotoUrl)
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.ivCommentAvatar)
            } else {
                binding.ivCommentAvatar.setImageResource(R.drawable.ic_profile)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<SpotComment>() {
        override fun areItemsTheSame(oldItem: SpotComment, newItem: SpotComment): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SpotComment, newItem: SpotComment): Boolean = oldItem == newItem
    }
}

