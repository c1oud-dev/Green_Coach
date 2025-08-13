package com.application.frontend.data.detail

import com.application.frontend.model.StepSection
import com.application.frontend.model.SubCategoryDetail

fun SubCategoryDetailDto.toModel(): SubCategoryDetail =
    SubCategoryDetail(
        key, name, imageUrl, headerColor, subtitle,
        steps.map { StepSection(it.title, it.bullets) }, wrongExamples
    )