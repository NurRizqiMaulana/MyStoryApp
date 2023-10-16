package com.dicoding.mystoryapp.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.data.remote.response.ListStoryItem
import com.dicoding.mystoryapp.databinding.ItemRowStoryBinding
import com.dicoding.mystoryapp.ui.detail.DetailActivity

class StoryAdapter: ListAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        Glide.with(holder.itemView.context).load(item.photoUrl).into(holder.imageStory)
        holder.tvNameStory.text = item.name
        holder.tvDescStory.text = item.description
        holder.bind(item)

    }

    inner class ViewHolder(binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageStory = binding.ivItemPhoto
        val tvNameStory = binding.tvItemName
        val tvDescStory = binding.tvItemDescription

        fun bind(story: ListStoryItem) {
            itemView.setOnClickListener {

                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_STORY, story)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(imageStory, "photo"),
                        Pair(tvNameStory, "name"),
                        Pair(tvDescStory, "description"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}