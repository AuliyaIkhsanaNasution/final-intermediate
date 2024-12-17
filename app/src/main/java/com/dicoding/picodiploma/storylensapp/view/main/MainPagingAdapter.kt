package com.dicoding.picodiploma.storylensapp.view.main

import android.widget.ImageView
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.dicoding.picodiploma.storylensapp.R
import com.dicoding.picodiploma.storylensapp.data.response.ListStoryItem
import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.storylensapp.view.DetailActivity
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair

class MainPagingAdapter : PagingDataAdapter<ListStoryItem, MainPagingAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return StoryViewHolder(view)
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgItemPhoto: ImageView = itemView.findViewById(R.id.iv_item_photo)
        private val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
        private val tvItemDescription: TextView = itemView.findViewById(R.id.tv_desc)
        private val tvCreatedAt: TextView = itemView.findViewById(R.id.tv_createat)

        fun bind(story: ListStoryItem) {
            // Mengisi data ke dalam tampilan
            Glide.with(itemView)
                .load(story.photoUrl)
                .into(imgItemPhoto)
            tvItemName.text = story.name
            tvItemDescription.text = story.description
            tvCreatedAt.text = story.createdAt

            // Membuka detail cerita saat item diklik
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                    putExtra("STORY_ID", story.id)
                }
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(imgItemPhoto, "image"),
                        Pair(tvItemName, "title"),
                        Pair(tvItemDescription, "desc"),
                        Pair(tvCreatedAt, "createat")
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position) // Mengambil item dari PagingDataAdapter
        if (story != null) { // Memastikan item tidak null
            holder.bind(story)
        }
    }
}
