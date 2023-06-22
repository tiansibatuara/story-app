package com.example.storyapp.models

import androidx.paging.PagingSource
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.data.source.local.ApiService
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1
