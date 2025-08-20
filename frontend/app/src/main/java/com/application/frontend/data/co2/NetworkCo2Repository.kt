package com.application.frontend.data.co2

import com.application.frontend.model.Co2Snapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface Co2Repository {
    suspend fun world(): Co2Snapshot
    suspend fun korea(): Co2Snapshot
}

class NetworkCo2Repository (
    private val api: EmissionsApi,
    private val io: CoroutineDispatcher
) : Co2Repository {
    override suspend fun world(): Co2Snapshot = withContext(io) { api.getWorld().toDomain() }
    override suspend fun korea(): Co2Snapshot = withContext(io) { api.getKorea().toDomain() }
}