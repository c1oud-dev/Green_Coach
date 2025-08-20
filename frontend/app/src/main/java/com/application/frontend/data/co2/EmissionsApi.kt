package com.application.frontend.data.co2

import retrofit2.http.GET

interface EmissionsApi {
    @GET("/api/co2/world")
    suspend fun getWorld(): Co2SnapshotDto

    @GET("/api/co2/korea")
    suspend fun getKorea(): Co2SnapshotDto
}