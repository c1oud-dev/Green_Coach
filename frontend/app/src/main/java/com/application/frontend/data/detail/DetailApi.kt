package com.application.frontend.data.detail

import retrofit2.http.GET
import retrofit2.http.Path

interface DetailApi {
    @GET("/api/subcategories/{key}/detail")
    suspend fun detail(@Path("key") key: String): SubCategoryDetailDto
}