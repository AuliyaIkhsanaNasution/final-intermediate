package com.dicoding.picodiploma.storylensapp.view.maps

import com.dicoding.picodiploma.storylensapp.data.repository.LocationRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.storylensapp.data.response.ListStoryItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch

class MapsViewModel (private val locationRepository: LocationRepository) : ViewModel() {

    private val _mapList = MutableLiveData<List<ListStoryItem>>()
    val mapList: LiveData<List<ListStoryItem>> = _mapList

    fun getLocation() {
        viewModelScope.launch {
            try {
                val stories = locationRepository.getLocation()
                _mapList.value = stories
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}