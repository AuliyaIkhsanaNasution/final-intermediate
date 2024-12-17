package com.dicoding.picodiploma.storylensapp.view.signup

import com.dicoding.picodiploma.storylensapp.data.response.RegisterResponse
import retrofit2.HttpException
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storylensapp.data.repository.UserRepository

import androidx.lifecycle.MutableLiveData

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: MutableLiveData<RegisterResponse> get() = _registerResponse

    //fungsi register
    suspend fun register(name: String, email: String, password: String) {
        _isLoading.value = true // Set loading state to true
        try {
            val response = repository.register(name, email, password)
            _registerResponse.value = response
        } catch (e: HttpException) {
            _errorMessage.value = "Email sudah terdaftar atau password kurang dari 8 karakter"
        } catch (e: Exception) {
            _errorMessage.value = e.message ?: "Unknown error occurred"
        } finally {
            // Set loading state to false
            _isLoading.value = false
        }
    }
}