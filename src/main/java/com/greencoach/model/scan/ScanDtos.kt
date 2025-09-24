package com.greencoach.model.scan

import java.time.Instant
import jakarta.validation.constraints.NotBlank

/**
 * 업로드와 함께 올 수 있는 간단한 메타 정보 (필요 없으면 사용 안 해도 됨)
 */
data class ScanMetadata(
    val userId: String? = null,
    val notes: String? = null
)

/**
 * AI가 반환한 단일 예측 결과
 */
data class Prediction(
    @field:NotBlank
    val label: String,
    val confidence: Double,
    val category: String? = null
)

/**
 * 최종 응답: 예측 리스트 + 처리 정보
 */
data class ScanResponse(
    val items: List<Prediction>,
    val model: String,
    val processedAt: Instant = Instant.now()
)