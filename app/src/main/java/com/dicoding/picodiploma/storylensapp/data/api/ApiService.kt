package com.dicoding.picodiploma.storylensapp.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import com.dicoding.picodiploma.storylensapp.data.response.ListStoryResponse
import com.dicoding.picodiploma.storylensapp.data.response.LoginResponse
import com.dicoding.picodiploma.storylensapp.data.response.RegisterResponse
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import com.dicoding.picodiploma.storylensapp.data.response.AddResponse
import com.dicoding.picodiploma.storylensapp.data.response.DetailtStoryResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    //api service untuk proses login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String): LoginResponse

    //api service untuk register
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String): RegisterResponse

    //api service untuk detail story
    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") storyId: String): DetailtStoryResponse

    @GET("stories")
    suspend fun getLocation(
        @Query("location") location: Int = 1, ): ListStoryResponse

    //api service untuk menambahkan story
    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody, ): AddResponse

    //api service untuk ambil list story
    @GET("stories")
    suspend fun getListStory(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20): ListStoryResponse
}