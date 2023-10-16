package com.dicoding.mystoryapp.data.repository

import androidx.lifecycle.liveData
import com.dicoding.mystoryapp.data.local.datastore.UserModel
import com.dicoding.mystoryapp.data.local.datastore.UserPreference
import com.dicoding.mystoryapp.data.remote.response.ErrorResponse
import com.dicoding.mystoryapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun register(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val responseBody = apiService.register(name, email, password)
            emit(Result.Success(responseBody))
        } catch (e: HttpException) {

            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message))

        }
    }

    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val responseBody = apiService.login(email,password)
            emit(Result.Success(responseBody))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }

    suspend fun saveSession(user: UserModel){
        userPreference.saveSession(user)
    }

    fun getSession():Flow<UserModel>{
        return userPreference.getSession()
    }

    suspend fun logout(){
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(apiService: ApiService,userPreference: UserPreference) : UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService,userPreference)
            }.also { instance = it }
    }
}