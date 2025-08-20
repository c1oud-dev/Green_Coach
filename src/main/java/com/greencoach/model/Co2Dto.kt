package com.greencoach.model

data class Co2PointDto(val year: Int, val value: Double)
data class Co2SeriesDto(val label: String, val points: List<Co2PointDto>)
data class Co2SnapshotDto(
    val emissions: Co2SeriesDto,
    val reduction: Co2SeriesDto
)
