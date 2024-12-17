package com.dicoding.picodiploma.storylensapp.data.api

import android.util.Log
import com.dicoding.picodiploma.storylensapp.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient


class ApiConfig(private val baseUrl: String = "https://story-api.dicoding.dev/v1/") {

    // Fungsi untuk mendapatkan instance ApiService dengan token
    fun getApiService(token: String): ApiService {
        // Logging token untuk debugging
        Log.d("ApiConfig", "Token value: $token")

        // Buat interceptor untuk logging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        // Buat interceptor untuk menambahkan header Authorization
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(requestHeaders)
        }

        // Konfigurasi OkHttpClient
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        // Konfigurasi Retrofit
        val retrofit = Retrofit.Builder()
            // Menggunakan baseUrl yang diberikan
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
