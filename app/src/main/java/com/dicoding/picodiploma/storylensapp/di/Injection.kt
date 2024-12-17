package com.dicoding.picodiploma.storylensapp.di

import com.dicoding.picodiploma.storylensapp.data.repository.UserRepository
import com.dicoding.picodiploma.storylensapp.data.pref.UserPreference
import android.content.Context
import com.dicoding.picodiploma.storylensapp.data.api.ApiConfig
import com.dicoding.picodiploma.storylensapp.data.api.ApiService
import kotlinx.coroutines.flow.first
import com.dicoding.picodiploma.storylensapp.data.pref.dataStore
import com.dicoding.picodiploma.storylensapp.data.repository.ListStoryRepository
import com.dicoding.picodiploma.storylensapp.data.repository.LocationRepository
import kotlinx.coroutines.runBlocking

object Injection {

    fun provideStoryRepository(context: Context): ListStoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig().getApiService(user.token)
        return ListStoryRepository.getInstance(apiService, pref)
    }

    fun provideLocationRepository(context: Context): LocationRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig().getApiService(user.token)
        return LocationRepository.getInstance(apiService, pref)
    }

    fun userRepositoryProvide(context: Context, apiService: ApiService): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref,apiService)
    }
}