package com.greencoach.service

import com.greencoach.model.CategoryDto
import org.springframework.stereotype.Service

@Service
class CategoryService {
    // 1) 최상위 카테고리 목록
    private val topCategories = listOf(
        CategoryDto("투명 페트병", "/images/icons/pet_bottle.png"),
        CategoryDto("플라스틱",   "/images/icons/plastic_container.png"),
        CategoryDto("비닐류",     "/images/icons/bag.png"),
        CategoryDto("스티로폼",   "/images/icons/styrofoam.png"),
        CategoryDto("캔류",       "/images/icons/can.png"),
        CategoryDto("고철류",     "/images/icons/steel.png"),
        CategoryDto("유리병",     "/images/icons/glass_bottle.png"),
        CategoryDto("종이류",     "/images/icons/paper.png"),
        CategoryDto("옷/섬유류",  "/images/icons/fabric.png"),
        CategoryDto("소형 전자제품", "/images/icons/small_appliance.png"),
        CategoryDto("대형 전자제품", "/images/icons/large_appliance.png"),
        CategoryDto("가구",       "/images/icons/furniture.png"),
        CategoryDto("전지류",     "/images/icons/battery.png"),
        CategoryDto("음식물",     "/images/icons/food.png"),
    )

    // 2) 서브 카테고리 매핑
    private val subCategoriesMap: Map<String, List<CategoryDto>> = mapOf(
        "투명 페트병" to listOf(
            CategoryDto("생수병",     "/images/sub/pet_water.png"),
            CategoryDto("음료수병",   "/images/sub/pet_drink.png"),
            CategoryDto("투명 우유병", "/images/sub/pet_milk.png"),
            CategoryDto("막걸리병",   "/images/sub/pet_makgeolli.jpg"),
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