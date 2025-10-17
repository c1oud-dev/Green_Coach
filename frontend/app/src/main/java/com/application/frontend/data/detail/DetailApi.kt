package com.application.frontend.data.detail

import com.application.frontend.model.SearchResultDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DetailApi {
    @GET("/api/subcategories/{key}/detail")
    suspend fun detail(@Path("key") key: String): SubCategoryDetailDto

    // 키워드 검색 → 상세 이동에 필요한 key/name 응답
    @GET("/api/subcategories/search")
    suspend fun search(@Query("keyword") keyword: String): Response<SearchResultDto>
}