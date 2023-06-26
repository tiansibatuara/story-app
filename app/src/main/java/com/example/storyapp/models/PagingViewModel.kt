package com.example.storyapp.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.paging.StoryRepository
import com.example.storyapp.utils.UserSharedPreferences

class PagingViewModel(application: Application, storiesRepository: StoryRepository, token: String) : AndroidViewModel(application) {

    val stories: LiveData<PagingData<ListStoryItem>> =
        storiesRepository.getStories(token).cachedIn(viewModelScope)

    fun removeUserSession() {
        val userPreference = UserSharedPreferences(getApplication())
        userPreference.clearUser()
    }
}