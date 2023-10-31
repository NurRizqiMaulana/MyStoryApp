package com.dicoding.mystoryapp.data.di

import android.content.Context
import com.dicoding.mystoryapp.data.local.datastore.UserPreference
import com.dicoding.mystoryapp.data.local.datastore.dataStore
import com.dicoding.mystoryapp.data.local.room.StoryDatabase
import com.dicoding.mystoryapp.data.remote.retrofit.ApiConfig
import com.dicoding.mystoryapp.data.repository.StoryRepository
import com.dicoding.mystoryapp.data.repository.UserRepository

object Injection {

    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService,pref)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getInstance(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService,database)
    }
}