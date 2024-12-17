package com.dicoding.picodiploma.storylensapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import com.dicoding.picodiploma.storylensapp.data.pref.UserPreference
import androidx.paging.PagingConfig
import com.dicoding.picodiploma.storylensapp.data.response.DetailtStoryResponse
import com.dicoding.picodiploma.storylensapp.data.response.ListStoryItem
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.storylensapp.data.StoryPagingSource
import com.dicoding.picodiploma.storylensapp.data.api.ApiService
import kotlinx.coroutines.flow.firstOrNull

class ListStoryRepository private constructor(
    // Tambahkan properti ApiService dan UserPreference
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun getStoryPager(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).liveData
    }

    companion object {
        fun getInstance(apiService: ApiService, userPreference: UserPreference): ListStoryRepository {
            return ListStoryRepository(apiService, userPreference)
        }
    }

    // Fungsi untuk mengambil detail cerita berdasarkan ID dari API
    suspend fun getStoryById(storyId: String): DetailtStoryResponse {
        try {
            userPreference.getSession().firstOrNull()?.token
                ?: throw NullPointerException("Token is null")
            return apiService.getDetailStory(storyId)
        } catch (e: Exception) {
            throw e
        }
    }
}