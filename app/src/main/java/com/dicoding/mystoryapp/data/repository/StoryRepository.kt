package com.dicoding.mystoryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.mystoryapp.data.local.room.StoryDatabase
import com.dicoding.mystoryapp.data.paging.StoryRemoteMediator
import com.dicoding.mystoryapp.data.remote.response.ErrorResponse
import com.dicoding.mystoryapp.data.remote.response.ListStoryItem
import com.dicoding.mystoryapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File


class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase

){
    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String) : LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
//                QuotePagingSource(apiService)
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData

//        emit(Result.Loading)
//        try {
//            val responseBody = apiService.getStories("Bearer $token")
//            emit(Result.Success(responseBody.listStory))
//        } catch (e: HttpException) {
//
//            val errorBody = e.response()?.errorBody()?.string()
//            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
//            emit(Result.Error(errorResponse.message))
//        }
    }


    fun addNewStory(token: String, description: String, imageFile: File) = liveData {
            emit(Result.Loading)
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            try {
                val successResponse = apiService.addNewStory("Bearer $token", requestBody,multipartBody)
                emit(Result.Success(successResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                emit(Result.Error(errorResponse.message))

            }
        }

    fun addNewStoryWithLocation(token: String, description: String, imageFile: File, lat: String, lon: String) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val latitude = lat.toRequestBody("text/plain".toMediaType())
        val longtitude = lon.toRequestBody("text/plain".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.addNewStoryWithLocation("Bearer $token", requestBody,multipartBody,latitude,longtitude)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message))

        }
    }

    fun getStoriesWithLocation(token: String) = liveData {
        emit(Result.Loading)
        try {
            val responseBody = apiService.getStoriesWithLocation("Bearer $token")
            emit(Result.Success(responseBody.listStory))
        } catch (e: HttpException) {

            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ) : StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService,storyDatabase)
            }.also { instance = it }
    }
}