package com.dicoding.picodiploma.storylensapp.view.main

import com.dicoding.picodiploma.storylensapp.data.response.DetailtStoryResponse
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storylensapp.data.pref.UserModel
import androidx.lifecycle.LiveData
import com.dicoding.picodiploma.storylensapp.data.response.ListStoryItem
import androidx.lifecycle.asLiveData
import com.dicoding.picodiploma.storylensapp.data.repository.ListStoryRepository
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.storylensapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository,
                    private val storyRepository: ListStoryRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    val getStoryPager: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStoryPager().cachedIn(viewModelScope)

    // Fungsi untuk mengambil detail cerita berdasarkan ID
    suspend fun getStoryById(storyId: String): DetailtStoryResponse {
        try {
            return storyRepository.getStoryById(storyId)
        } catch (e: Exception) {
            throw e
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData() }

    fun logout() {
        viewModelScope.launch {
            repository.logout() }
    }
}