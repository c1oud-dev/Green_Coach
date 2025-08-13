package com.application.frontend.model

data class StepSection(
    val title: String,
    val bullets: List<String>
)

data class SubCategoryDetail(
    val key: String,
    val name: String,
    val imageUrl: String,     // 서버 URL
    val headerColor: String,  // "#RRGGBB"
    val subtitle: String,
    val steps: List<StepSection>,
    val wrongExamples: List<String>
)
