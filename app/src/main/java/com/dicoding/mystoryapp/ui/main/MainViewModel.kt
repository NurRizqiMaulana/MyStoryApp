package com.dicoding.mystoryapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.data.local.datastore.UserModel
import com.dicoding.mystoryapp.data.repository.StoryRepository
import com.dicoding.mystoryapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
    ) : ViewModel() {

    fun register(name: String, email: String, password: String) = userRepository.register(name, email, password)

    fun login(email: String, password: String) = userRepository.login( email, password)

    fun getSession(): LiveData<UserModel> = userRepository.getSession().asLiveData()

    fun setlogin(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }
    fun deleteLogin() { viewModelScope.launch { userRepository.logout() } }

    fun getStories(token: String) = storyRepository.getStories(token)

    fun addNewStory(token: String, description: String, photo: File) = storyRepository.addNewStory(token, description, photo)


}