package com.greencoach.service

import com.greencoach.model.CategoryDto
import org.springframework.stereotype.Service

@Service
class CategoryService {

    // 1) 최상위 카테고리 목록
    private val topCategories = listOf(
        /*CategoryDto("페트병", "ic_pet"),
        CategoryDto("플라스틱 용기",      "ic_plastic"),
        CategoryDto("비닐류",              "ic_bag"),
        CategoryDto("스티로폼",            "ic_styro"),
        CategoryDto("캔류",                "ic_can"),
        CategoryDto("유리병",              "ic_glass"),
        CategoryDto("종이류",              "ic_paper"),
        CategoryDto("박스/골판지",         "ic_milk"),
        CategoryDto("옷/섬유류",           "ic_box"),
        CategoryDto("소형가전",            "ic_cloth"),
        CategoryDto("대형가전",            "ic_bulb"),
        CategoryDto("형광등/전구",         "ic_washer")*/

        CategoryDto("페트병", "/images/icons/pet_bottle.png"),
        CategoryDto("플라스틱 용기",      "/images/icons/plastic_container.png"),
        CategoryDto("비닐류",              "/images/icons/bag.png"),
        CategoryDto("스티로폼",            "/images/icons/styrofoam.png"),
        CategoryDto("캔류",                "/images/icons/can.png"),
        CategoryDto("유리병",              "/images/icons/glass_bottle.png"),
        CategoryDto("종이류",              "/images/icons/paper.png"),
        CategoryDto("박스/골판지",         "/images/icons/cardboard.png"),
        CategoryDto("옷/섬유류",           "/images/icons/fabric.png"),
        CategoryDto("소형가전",            "/images/icons/small_appliance.png"),
        CategoryDto("대형가전",            "/images/icons/large_appliance.png"),
        CategoryDto("형광등/전구",         "/images/icons/bulb.png")
    )

    // 2) 서브 카테고리 매핑
    private val subCategoriesMap: Map<String, List<CategoryDto>> = mapOf(
        "페트병" to listOf(
            CategoryDto("생수",    "/images/sub/pet_water.png"),
            /*CategoryDto("음료수",  "/images/sub/pet_drink.png"),
            CategoryDto("맥주",    "/images/sub/pet_beer.png"),
            CategoryDto("소주",    "/images/sub/pet_soju.png"),
            CategoryDto("탄산",    "/images/sub/pet_soda.png"),
            CategoryDto("요구르트","/images/sub/pet_yogurt.png")*/
        ),
        // …다른 topCategories 키에 대해 매핑
    )

    /** 모든 최상위 카테고리 반환 */
    fun getTopCategories(): List<CategoryDto> =
        topCategories

    /**
     * 선택된 카테고리에 대한 서브 카테고리 반환
     * @param categoryName topCategories 중 하나의 name
     */
    fun getSubCategories(categoryName: String): List<CategoryDto> =
        subCategoriesMap[categoryName] ?: emptyList()
}