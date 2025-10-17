package com.greencoach.service

import com.greencoach.model.SearchResultDto
import com.greencoach.model.StepSectionDto
import com.greencoach.model.SubCategoryDetailDto
import org.springframework.stereotype.Service

@Service
class SubCategoryDetailService {

    // 간단한 별칭/키워드 매핑 (확장 가능)
private val aliasToKey: Map<String, String> = mapOf(
        "투명 페트병" to "pet_water",
        "페트병"     to "pet_water",
        "생수병"     to "pet_water"
    )

fun search(keyword: String): SearchResultDto? {
        val q = keyword.trim().lowercase()
        // 완전/부분 일치 허용
        val matchedKey = aliasToKey.entries.firstOrNull {
                val k = it.key.lowercase()
                k == q || k.contains(q) || q.contains(k)
            }?.value ?: return null
        val detail = getDetail(matchedKey) ?: return null
        return SearchResultDto(key = detail.key, name = detail.name)
    }
    fun getDetail(key: String): SubCategoryDetailDto? = when (key) {
        "pet_water" -> SubCategoryDetailDto(
            key = key, name = "투명 페트병",
            imageUrl = "/images/sub/transparent_pet.png",
            headerColor = "#66CBD2",
            subtitle = "투명 페트병은 고품질 재활용이 가능한 자원이기 때문에, 이물질 없이 깨끗하게 분리배출하는 것이 중요해요.",
            steps = listOf(
                StepSectionDto("내용물 비우기", listOf(
                    "병 안에 음료가 남아 있지 않도록 완전히 비워주세요.",
                    "커피, 우유, 막걸리 등 이물질이 섞인 경우에는 세척한 뒤 배출하거나 일반 쓰레기로 버려야 해요."
                )),
                StepSectionDto("라벨 제거", listOf(
                    "대부분의 투명 페트병은 점선이 있어 손쉽게 라벨을 뜯을 수 있어요.",
                    "라벨은 반드시 제거해서 비닐류로 따로 배출해야 해요. (※ PET병 본체와 라벨은 재질이 달라요)"
                )),
                StepSectionDto("뚜껑 분리 & 찌그러뜨리기", listOf(
                    "뚜껑도 분리해서 함께 플라스틱류로 배출해주세요.",
                    "병은 눌러서 찌그러뜨리면 공간 절약에 좋아요."
                ))
            ),
            wrongExamples = listOf(
                "라벨이나 뚜껑을 제거하지 않거나, 내용물이 남은 상태로 배출하면 재활용이 어렵고 이물질이 섞여 오염될 경우, " +
                        "전체가 폐기물로 처리될 수 있어요. 특히 생수병·음료병·우유병·막걸리병은 외형이 비슷하지만 내용물에 " +
                        "따라 분리배출 기준이 달라질 수 있으니 주의해야 해요."
            )
        )
        else -> null
    }
}