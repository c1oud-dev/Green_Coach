package com.application.frontend.data.detail

data class StepSectionDto(val title: String, val bullets: List<String>)
data class SubCategoryDetailDto(
    val key: String, val name: String,
    val imageUrl: String, val headerColor: String,
    val subtitle: String, val steps: List<StepSectionDto>,
    val wrongExamples: List<String>
)
