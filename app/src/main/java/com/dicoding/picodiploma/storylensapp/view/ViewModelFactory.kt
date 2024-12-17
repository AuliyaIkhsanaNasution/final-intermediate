package com.dicoding.picodiploma.storylensapp.view

import com.dicoding.picodiploma.storylensapp.view.main.MainViewModel
import com.dicoding.picodiploma.storylensapp.data.api.ApiService
import com.dicoding.picodiploma.storylensapp.data.repository.ListStoryRepository
import com.dicoding.picodiploma.storylensapp.di.Injection
import android.content.Context
import com.dicoding.picodiploma.storylensapp.data.repository.LocationRepository
import com.dicoding.picodiploma.storylensapp.view.maps.MapsViewModel
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storylensapp.view.login.LoginViewModel
import com.dicoding.picodiploma.storylensapp.view.signup.SignupViewModel
import com.dicoding.picodiploma.storylensapp.data.repository.UserRepository
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val repository: UserRepository, private val storyRepository: ListStoryRepository,
                       private val locationRepository: LocationRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context, apiService: ApiService): ViewModelFactory {
            clearInstance()
            synchronized(ViewModelFactory::class.java) {
                val userRepository = Injection.userRepositoryProvide(context, apiService)
                val storyRepository = Injection.provideStoryRepository(context)
                val locationRepository = Injection.provideLocationRepository(context)
                INSTANCE = ViewModelFactory(userRepository,storyRepository, locationRepository )
            }
            return INSTANCE as ViewModelFactory
        }
        @JvmStatic
        private fun clearInstance() {
            INSTANCE = null
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(repository,storyRepository) as T
            modelClass.isAssignableFrom(SignupViewModel::class.java) ->
                SignupViewModel(repository) as T
            modelClass.isAssignableFrom(MapsViewModel::class.java) ->
                MapsViewModel(locationRepository) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}