package com.dicoding.mystoryapp.data.repository

import androidx.lifecycle.liveData
import com.dicoding.mystoryapp.data.remote.response.ErrorResponse
import com.dicoding.mystoryapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File


class StoryRepository private constructor(
    private val apiService: ApiService

){
    fun getStories(token: String) = liveData {
        emit(Result.Loading)
        try {
            val responseBody = apiService.getStories("Bearer $token")
            emit(Result.Success(responseBody.listStory))
        } catch (e: HttpException) {

            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
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

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService
        ) : StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}