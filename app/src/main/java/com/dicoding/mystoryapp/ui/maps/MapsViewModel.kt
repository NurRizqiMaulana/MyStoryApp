package com.dicoding.mystoryapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.data.local.datastore.UserModel
import com.dicoding.mystoryapp.data.repository.StoryRepository
import com.dicoding.mystoryapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MapsViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository): ViewModel() {
    fun getStoriesWithLocation(token: String) = storyRepository.getStoriesWithLocation(token)

    fun getSession(): LiveData<UserModel> = userRepository.getSession().asLiveData()

    fun deleteLogin() { viewModelScope.launch { userRepository.logout() } }
}