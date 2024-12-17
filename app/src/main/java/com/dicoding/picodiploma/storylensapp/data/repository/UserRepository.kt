package com.dicoding.picodiploma.storylensapp.data.repository

import com.dicoding.picodiploma.storylensapp.data.response.LoginResponse
import com.dicoding.picodiploma.storylensapp.data.response.RegisterResponse
import android.util.Log
import com.dicoding.picodiploma.storylensapp.data.pref.UserModel
import kotlinx.coroutines.flow.Flow
import com.dicoding.picodiploma.storylensapp.data.pref.UserPreference
import retrofit2.HttpException
import com.dicoding.picodiploma.storylensapp.data.api.ApiService

class UserRepository private constructor(

    // Tambahkan properti ApiService dan UserPreference
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    // Fungsi untuk mendapatkan sesi pengguna dari UserPreference
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    // Fungsi untuk menyimpan sesi pengguna ke UserPreference
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository( userPreference ,apiService)
            }.also { instance = it }
    }

    // Fungsi untuk melakukan login pengguna
    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val response = apiService.login(email, password)
            response
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("UserRepository", "Login failed. Error body: $errorBody")
            throw e
        }
    }

    // Fungsi untuk melakukan logout pengguna
    suspend fun logout() {
        userPreference.logout()
    }

    // Fungsi untuk melakukan registrasi pengguna
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return try {
            val response = apiService.register(name, email, password)
            response
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("SignupViewModel", "Registration failed. Error body: $errorBody")
            throw e
        }
    }
}