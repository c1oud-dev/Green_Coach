package com.application.frontend.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.data.BackendApi
import com.application.frontend.model.NewsDto
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsViewModel: ViewModel() {
    var news by mutableStateOf<List<NewsDto>>(emptyList())
        private set

    private val api: BackendApi = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")   // 로컬 테스트: http://10.0.2.2:8080/
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BackendApi::class.java)

    init {
        fetch("분리배출")
    }

    fun fetch(query: String) {
        viewModelScope.launch {
            // I/O 전용 디스패처로 옮겨서 네트워크 호출
            val result = withContext(Dispatchers.IO) {
                runCatching { api.getNews(query) }
                    .onFailure { Log.e("NewsAPI", "fetch error", it) }
                    .getOrDefault(emptyList())
            }
            // 호출 결과를 메인 스레드에서 state 업데이트
            news = result
        }
    }
}