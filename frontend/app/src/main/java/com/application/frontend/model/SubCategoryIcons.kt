package com.application.frontend.model

import com.application.frontend.R

object SubCategoryIcons {
    // 서버가 주는 파일명(확장자/경로 제외) -> 드로어블 ID
    val byKey = mapOf(
        "pet_water" to R.drawable.ic_pet_water,
        "pet_drink"     to R.drawable.ic_pet_drink,
        "pet_milk"     to R.drawable.ic_pet_milk,
        "pet_makgeolli"     to R.drawable.ic_pet_makgeolli,
        // TODO: 나머지도 여기에 계속 추가
    )
}