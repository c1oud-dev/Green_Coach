package com.application.frontend.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.application.frontend.data.detail.SubCategoryDetailRepository
import com.application.frontend.model.StepSection
import com.application.frontend.model.SubCategoryDetail
import com.application.frontend.viewmodel.SubCategoryDetailViewModel
import org.junit.Rule
import org.junit.Test

class SubCategoryDetailScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun fakeDetail() = SubCategoryDetail(
        key = "bottle",
        name = "성공이름",
        imageUrl = "/img.png",
        headerColor = "#66CBD2",
        subtitle = "성공설명",
        steps = listOf(StepSection("1단계", listOf("A", "B"))),
        wrongExamples = listOf("X")
    )

    @Test
    fun 성공상태면_콘텐츠표시() {
        // 항상 성공 반환하는 가짜 레포
        val vm = SubCategoryDetailViewModel(object : SubCategoryDetailRepository {
            override suspend fun getDetail(key: String): SubCategoryDetail? = fakeDetail()
        })

        composeRule.setContent {
            SubCategoryDetailScreen(
                key = "bottle",
                name = "unused",
                onBack = {},
                vm = vm
            )
        }

        composeRule.onNodeWithText("성공이름").assertIsDisplayed()
        composeRule.onNodeWithText("성공설명").assertIsDisplayed()

        // 👇 관찰용 잠깐 대기(끝나면 제거)
        Thread.sleep(2000)
    }

    @Test
    fun 에러상태면_에러문구와_버튼표시() {
        val vm = SubCategoryDetailViewModel(object : SubCategoryDetailRepository {
            override suspend fun getDetail(key: String): SubCategoryDetail? = null
        })

        composeRule.setContent {
            SubCategoryDetailScreen(
                key = "bottle",
                name = "unused",
                onBack = {},
                vm = vm
            )
        }

        composeRule.onNodeWithText("상세 데이터를 찾을 수 없어요. 잠시 후 다시 시도해주세요.").assertIsDisplayed()
        composeRule.onNodeWithText("뒤로가기").assertIsDisplayed()
        composeRule.onNodeWithText("재시도").assertIsDisplayed()

        // 👇 관찰용 잠깐 대기(끝나면 제거)
        Thread.sleep(2000)
    }
}