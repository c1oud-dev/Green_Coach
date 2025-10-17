package com.application.frontend.data

import com.application.frontend.model.SubCategoryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryApi {
    /** 최상위 카테고리 목록 */
    @GET("/api/categories")
    suspend fun getTopCategories(): List<SubCategoryDto>

    /** 서브 카테고리 목록 */
    @GET("/api/categories/{name}/sub")
    suspend fun getSubCategories(
        @Path("name") categoryName: String
    ): List<SubCategoryDto>
}
