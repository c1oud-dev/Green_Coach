package com.greencoach.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CategoryServiceTest {

    private val service = CategoryService()

    @Nested
    @DisplayName("getTopCategories")
    inner class GetTopCategories {

        @Test
        fun `최상위 카테고리 목록을 반환한다`() {
            val result = service.getTopCategories()

            assertThat(result).isNotEmpty
            // 대표 항목 몇 개만 검증(전체 리스트에 과도하게 결합하지 않기)
            assertThat(result.map { it.name })
                .contains("페트병", "플라스틱 용기", "캔류")
            // 아이콘 경로 형태 검증
            assertThat(result.all { it.imageUrl.startsWith("/images/icons/") }).isTrue
        }
    }

    @Nested
    @DisplayName("getSubCategories")
    inner class GetSubCategories {

        @Test
        fun `존재하는 상위 카테고리(페트병)면 서브 카테고리를 반환한다`() {
            val result = service.getSubCategories("페트병")

            assertThat(result).isNotEmpty
            // "생수"가 있고 지정된 이미지가 매핑되는지
            assertThat(result.any { it.name == "생수" && it.imageUrl.endsWith("/images/sub/pet_water.png") })
                .isTrue
        }

        @Test
        fun `없는 상위 카테고리면 빈 리스트를 반환한다`() {
            val result = service.getSubCategories("없는카테고리")

            assertThat(result).isEmpty()
        }
    }
}