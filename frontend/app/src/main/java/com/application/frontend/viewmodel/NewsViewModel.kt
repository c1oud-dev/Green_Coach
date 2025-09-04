package com.application.frontend.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.data.BackendApi
import com.application.frontend.model.NewsDto
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val api: BackendApi
) : ViewModel() {
    var news by mutableStateOf<List<NewsDto>>(emptyList())
        private set

    init {
        fetch("분리배출")
    }

    fun fetch(query: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { api.getNews(query) }
                    .getOrDefault(emptyList())
            }
            news = result
        }
    }
}
