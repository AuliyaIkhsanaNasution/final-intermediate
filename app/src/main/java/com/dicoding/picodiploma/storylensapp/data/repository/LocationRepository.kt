package com.dicoding.picodiploma.storylensapp.data.repository

import com.dicoding.picodiploma.storylensapp.data.response.ListStoryItem
import android.util.Log
import com.dicoding.picodiploma.storylensapp.data.api.ApiService
import kotlinx.coroutines.flow.firstOrNull
import com.dicoding.picodiploma.storylensapp.data.pref.UserPreference

class LocationRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
){
    companion object {
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): LocationRepository {
            return LocationRepository(apiService, userPreference)
        }
    }

    suspend fun getLocation(): List<ListStoryItem> {
        try {
            val token = userPreference.getSession().firstOrNull()?.token
            Log.d("LocationRepository", "Retrieving token for getting location: $token")
            val storyResponse = apiService.getLocation()
            if (token == null) {
                Log.e("LocationRepository", "Token is null")
                throw NullPointerException("Token is null")
            }
            Log.d("LocationRepository", "Token: $token")
            return storyResponse.listStory
        } catch (e: Exception) {
            Log.e("LocationRepository", "Error getting location: ${e.message}", e)
            throw e
        }
    }
}