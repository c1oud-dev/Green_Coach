package com.application.frontend.data

import com.application.frontend.model.SubCategoryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryApi {
    /** 서버에서 JSON으로 sub-categories 반환 */
    @GET("/api/categories/{name}/sub")
    suspend fun getSubCategories(
        @Path("name") categoryName: String
    ): List<SubCategoryDto>
}