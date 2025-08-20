package com.application.frontend.data.co2

// 네트워크 DTO
data class Co2PointDto(val year: Int, val value: Double)
data class Co2SeriesDto(val label: String, val points: List<Co2PointDto>)
data class Co2SnapshotDto(
    val emissions: Co2SeriesDto,
    val reduction: Co2SeriesDto
)

// 로딩 상태
sealed interface Co2Result {
    data object Loading : Co2Result
    data class Success(val world: Co2SnapshotDto, val korea: Co2SnapshotDto) : Co2Result
    data class Error(val message: String) : Co2Result
}