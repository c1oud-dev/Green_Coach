package com.application.frontend.data

import com.application.frontend.model.NewsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface BackendApi {
    @GET("api/news")
    suspend fun getNews(
        @Query("query") query: String = "분리배출"
    ): List<NewsDto>
}