package com.dicoding.picodiploma.storylensapp.view

import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.storylensapp.view.main.MainViewModel
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.storylensapp.databinding.ActivityDetailBinding
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import android.os.Bundle
import com.dicoding.picodiploma.storylensapp.data.response.DetailtStoryResponse
import android.util.Log
import com.dicoding.picodiploma.storylensapp.data.api.ApiConfig
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    //init binding activity dan view model
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this, ApiConfig().getApiService("token"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val storyId = intent.getStringExtra("STORY_ID") ?: ""
        if (storyId.isNotEmpty()) {
            storyDetails(storyId)
        } else {
            Log.e("DetailActivity", "id dari story tidak ditemukan")
        }
    }

    private fun upUI(storyResponse: DetailtStoryResponse) {
        val story = storyResponse.story
        if (story != null) {
            binding.apply {
                tvDetailName.text = story.name
                tvDetailDescription.text = story.description
                creat.text = story.createdAt

                Glide.with(this@DetailActivity)
                    .load(story.photoUrl)
                    .into(ivDetailPhoto)
            }
        } else {
            Log.e("DetailActivity", "detail story tidak ditemukan")
        }
    }

    private fun storyDetails(storyId: String) {
        lifecycleScope.launch {
            try {
                val response = viewModel.getStoryById(storyId)
                upUI(response)
            } catch (e: Exception) {
                Log.e("DetailActivity", "Error fetching story details: ${e.message}", e)
            }
        }
    }
}