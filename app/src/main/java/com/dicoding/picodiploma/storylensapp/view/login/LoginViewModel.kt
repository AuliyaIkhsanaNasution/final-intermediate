package com.dicoding.picodiploma.storylensapp.view.login

import com.dicoding.picodiploma.storylensapp.data.response.LoginResponse
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storylensapp.data.pref.UserModel
import com.dicoding.picodiploma.storylensapp.data.repository.UserRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    // Fungsi untuk menyimpan sesi pengguna
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    // Fungsi untuk melakukan login
    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val response = repository.login(email, password)
            if (response.loginResult != null) {
                response.loginResult.token?.let { token ->
                    val user = UserModel(email, token, true)
                    //panggil fungsi saveSession
                    saveSession(user)
                }
            }
            response
        } catch (e: Exception) {
            throw e
        }
    }
}