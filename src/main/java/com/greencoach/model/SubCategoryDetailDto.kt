package com.greencoach.model

data class StepSectionDto(val title: String, val bullets: List<String>)
data class SubCategoryDetailDto(
    val key: String, val name: String,
    val imageUrl: String,          // 예: /images/sub/pet_water.png
    val headerColor: String,       // "#58C4C4"
    val subtitle: String,
    val steps: List<StepSectionDto>,
    val wrongExamples: List<String>
)

// 검색 결과(상세 화면으로 이동하기 위한 최소 정보)
data class SearchResultDto(
    val key: String,
    val name: String
)