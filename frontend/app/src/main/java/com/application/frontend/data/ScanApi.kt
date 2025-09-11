package com.application.frontend.data

import com.application.frontend.ui.screen.ScanHistoryDto
import com.application.frontend.ui.screen.ScanResultDto
import okhttp3.MultipartBody
import retrofit2.http.*

interface ScanApi {
    @Multipart
    @POST("/api/scan/analyze")
    suspend fun analyzeImage(
        @Part image: MultipartBody.Part
    ): ScanResultDto

    @GET("/api/scan/history")
    suspend fun getScanHistory(): List<ScanHistoryDto>

    @POST("/api/scan/history")
    suspend fun saveScanResult(
        @Body result: ScanResultDto
    ): ScanHistoryDto
}