package com.greencoach.service

import com.greencoach.model.StepSectionDto
import com.greencoach.model.SubCategoryDetailDto
import org.springframework.stereotype.Service

@Service
class SubCategoryDetailService {
    fun getDetail(key: String): SubCategoryDetailDto? = when (key) {
        "pet_water" -> SubCategoryDetailDto(
            key = key, name = "생수",
            imageUrl = "/images/sub/pet_water.png",
            headerColor = "#66CBD2",
            subtitle = "생수병은 재활용이 매우 중요한 자원이기 때문에, 깨끗하고 분리된 상태로 배출하는 것이 핵심이에요.",
            steps = listOf(
                StepSectionDto("내용물 비우기", listOf(
                    "병 안에 물이 남아 있지 않도록 완전히 비워주세요.",
                    "물뿐 아니라 이물질(음료, 우유, 커피 등)이 섞인 경우 세척 후 재활용, 아니면 일반 쓰레기로 버려야 해요."
                )),
                StepSectionDto("라벨 제거", listOf(
                    "대부분 생수병 라벨은 점선 처리가 되어 있어 손쉽게 찢을 수 있습니다.",
                    "라벨을 반드시 제거하고, 비닐류로 따로 분리배출해야 합니다. (같은 투명한 재질이라도 PET병과는 재질이 달라요.)"
                ))
            ),
            wrongExamples = listOf(
                "잘못된 상태처리 결과라벨 붙은 채 배출재활용 효율 급감, 분류 시 폐기될 수 있음",
                "내용물 남은 병오염물로 간주되어 일반 쓰레기 처리",
                "병 안에 빨대 등 이물질재활용 불가병을 통째로 묶어서 배출자동화 선별기에서 분류 실패 가능"
            )
        )
        else -> null
    }
}