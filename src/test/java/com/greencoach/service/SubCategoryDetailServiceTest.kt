package com.greencoach.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class SubCategoryDetailServiceTest {
    private val service = SubCategoryDetailService()

    @Test
    fun `getDetail returns dto for pet_water with expected fields`() {
        val dto = service.getDetail("pet_water")
        assertNotNull(dto, "pet_water 키에 대해 null이 반환되면 안 됩니다.")
        dto!!

        // 기본 필드
        assertEquals("pet_water", dto.key)
        assertEquals("생수", dto.name)
        assertEquals("/images/sub/pet_water.png", dto.imageUrl)
        assertEquals("#66CBD2", dto.headerColor)
        assertTrue(dto.subtitle.isNotBlank(), "subtitle은 비어있지 않아야 합니다.")

        // 단계(steps)
        assertEquals(2, dto.steps.size, "단계 섹션 수가 일치하지 않습니다.")
        assertEquals("내용물 비우기", dto.steps[0].title)
        assertTrue(dto.steps[0].bullets.isNotEmpty(), "첫 번째 단계 bullets가 비어있습니다.")
        assertEquals("라벨 제거", dto.steps[1].title)

        // 잘못된 배출 예시
        assertEquals(3, dto.wrongExamples.size, "잘못된 배출 예시 개수가 일치하지 않습니다.")
    }

    @Test
    fun `getDetail returns null for unknown key`() {
        val dto = service.getDetail("unknown_key")
        assertNull(dto, "알 수 없는 키에 대해서는 null이 반환되어야 합니다.")
    }
}