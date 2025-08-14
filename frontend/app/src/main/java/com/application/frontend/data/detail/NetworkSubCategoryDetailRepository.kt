package com.application.frontend.data.detail

import com.application.frontend.model.SubCategoryDetail
import javax.inject.Inject

interface SubCategoryDetailRepository { suspend fun getDetail(key: String): SubCategoryDetail? }

class NetworkSubCategoryDetailRepository @Inject constructor(
    private val api: DetailApi
) : SubCategoryDetailRepository {
    override suspend fun getDetail(key: String): SubCategoryDetail? =
        runCatching { api.detail(key).toModel() }.getOrNull()
}