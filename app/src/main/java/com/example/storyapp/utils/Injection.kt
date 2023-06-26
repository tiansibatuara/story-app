package com.example.storyapp.utils

import android.content.Context
import com.example.storyapp.data.source.local.ApiConfig
import com.example.storyapp.paging.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService)
    }
}