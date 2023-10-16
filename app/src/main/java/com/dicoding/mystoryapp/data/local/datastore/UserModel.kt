package com.dicoding.mystoryapp.data.local.datastore

data class UserModel (
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)