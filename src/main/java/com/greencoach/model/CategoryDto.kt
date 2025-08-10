package com.greencoach.model

/**
 * 프론트엔드 CategoryScreen.kt 의 Category 데이터 클래스와 매핑.
 * name: 화면에 표시할 카테고리명
 * imageUrl: 아이콘 또는 상세 이미지 URL (필요 시 프론트에서 로컬 리소스로 매핑)
 */

data class CategoryDto(
    val name: String,
    val imageUrl: String
    /*val iconName: String*/
)
