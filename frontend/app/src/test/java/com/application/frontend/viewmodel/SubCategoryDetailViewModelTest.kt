package com.application.frontend.viewmodel

import com.application.frontend.data.detail.SubCategoryDetailRepository
import com.application.frontend.model.StepSection
import com.application.frontend.model.SubCategoryDetail
import com.application.frontend.testing.MainDispatcherRule
import com.application.frontend.ui.state.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

private class SuccessFakeRepo(
    private val detail: SubCategoryDetail
) : SubCategoryDetailRepository {
    override suspend fun getDetail(key: String): SubCategoryDetail? = detail
}

private class NullFakeRepo : SubCategoryDetailRepository {
    override suspend fun getDetail(key: String): SubCategoryDetail? = null
}

private class ErrorFakeRepo(
    private val error: Throwable
) : SubCategoryDetailRepository {
    override suspend fun getDetail(key: String): SubCategoryDetail? {
        throw error
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SubCategoryDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun fakeDetail(name: String = "생수병") = SubCategoryDetail(
        key = "bottle",
        name = name,
        imageUrl = "/img.png",
        headerColor = "#66CBD2",
        subtitle = "설명",
        steps = listOf(StepSection("1단계", listOf("A", "B"))),
        wrongExamples = listOf("X")
    )

    @Test
    fun `정상 응답이면 Success 상태로 전환`() = runTest {
        val vm = SubCategoryDetailViewModel(SuccessFakeRepo(fakeDetail()))
        val states = mutableListOf<UiState<SubCategoryDetail>>()

        val job = launch { vm.uiState.collect { states.add(it) } }

        vm.load("bottle")
        advanceUntilIdle()

        assertTrue(states.first() is UiState.Loading)
        assertTrue(states.last() is UiState.Success)
        val data = (states.last() as UiState.Success).data
        assertEquals("생수병", data.name)

        job.cancel()
    }

    @Test
    fun `repo가 null을 반환하면 Error 상태`() = runTest {
        val vm = SubCategoryDetailViewModel(NullFakeRepo())
        val states = mutableListOf<UiState<SubCategoryDetail>>()

        val job = launch { vm.uiState.collect { states.add(it) } }

        vm.load("not-found")
        advanceUntilIdle()

        assertTrue(states.first() is UiState.Loading)
        assertTrue(states.last() is UiState.Error)

        job.cancel()
    }

    @Test
    fun `네트워크 오류(IOException)면 Error 상태와 메시지`() = runTest {
        val vm = SubCategoryDetailViewModel(ErrorFakeRepo(IOException("offline")))
        val states = mutableListOf<UiState<SubCategoryDetail>>()

        val job = launch { vm.uiState.collect { states.add(it) } }

        vm.load("bottle")
        advanceUntilIdle()

        val last = states.last()
        assertTrue(last is UiState.Error)
        assertTrue((last as UiState.Error).message.contains("네트워크"))

        job.cancel()
    }

    @Test
    fun `서버 오류(HttpException)면 Error 상태와 코드 반영`() = runTest {
        val errorBody = """{"error":"boom"}""".toResponseBody("application/json".toMediaType())
        val response: Response<Any> = Response.error(500, errorBody)
        val httpEx = HttpException(response)

        val vm = SubCategoryDetailViewModel(ErrorFakeRepo(httpEx))
        val states = mutableListOf<UiState<SubCategoryDetail>>()

        val job = launch { vm.uiState.collect { states.add(it) } }

        vm.load("bottle")
        advanceUntilIdle()

        val last = states.last()
        assertTrue(last is UiState.Error)
        assertTrue((last as UiState.Error).message.contains("500"))

        job.cancel()
    }

    @Test
    fun `retry 호출 시 load 재호출되어 상태가 갱신`() = runTest {
        var callCount = 0
        val vm = SubCategoryDetailViewModel(object : SubCategoryDetailRepository {
            override suspend fun getDetail(key: String): SubCategoryDetail? {
                callCount++
                return if (callCount == 1) null else fakeDetail("두번째성공")
            }
        })

        val states = mutableListOf<UiState<SubCategoryDetail>>()
        val job = launch { vm.uiState.collect { states.add(it) } }

        vm.load("bottle")
        advanceUntilIdle()
        assertTrue(states.last() is UiState.Error)

        vm.retry()
        advanceUntilIdle()
        assertTrue(states.last() is UiState.Success)
        assertEquals("두번째성공", (states.last() as UiState.Success).data.name)

        job.cancel()
    }
}