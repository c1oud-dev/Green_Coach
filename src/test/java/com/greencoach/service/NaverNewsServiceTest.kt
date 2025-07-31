package com.greencoach.service

import java.util.function.Function
import com.greencoach.model.NaverImageItem
import com.greencoach.model.NaverImageResponse
import com.greencoach.model.NaverNewsItem
import com.greencoach.model.NaverNewsResponse
import com.greencoach.model.NewsDto
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import java.net.URI

@ExtendWith(MockitoExtension::class)
class NaverNewsServiceTest {
    @Mock private lateinit var webClientBuilder: WebClient.Builder
    @Mock private lateinit var webClient: WebClient
    @Mock private lateinit var uriSpec: WebClient.RequestHeadersUriSpec<*>
    @Mock private lateinit var headersSpec: WebClient.RequestHeadersSpec<*>
    @Mock private lateinit var responseSpec: WebClient.ResponseSpec

    private lateinit var service: NaverNewsService

    @BeforeEach
    fun setUp() {
        // WebClient builder 목 설정
        whenever(webClientBuilder.baseUrl(any<String>())).thenReturn(webClientBuilder)
        whenever(webClientBuilder.defaultHeader(any(), any())).thenReturn(webClientBuilder)
        whenever(webClientBuilder.build()).thenReturn(webClient)

        service = NaverNewsService(
            webClientBuilder,
            clientId     = "test-id",
            clientSecret = "test-secret",
            baseUrl      = "https://api.test"
        )
    }

    @Test
    fun `search returns mapped NewsDto list`() = runBlocking {
        // given
        val query = "Spring"

        // — 뉴스 응답 목
        val newsItem = NaverNewsItem(
            title        = "테스트 뉴스",
            originallink = "https://news.example.com/article/1",
            description  = "desc",
            pubDate      = "Wed, 30 Jul 2025 10:00:00 +0900"
        )
        val newsResponse = NaverNewsResponse(items = listOf(newsItem))

        // — 이미지 응답 목
        val imageItem = NaverImageItem(
            link      = "https://img.example.com/1",
            thumbnail = "https://img.example.com/thumb.jpg",
            sizeheight = 100,
            sizewidth  = 100
        )
        val imageResponse = NaverImageResponse(items = listOf(imageItem))

        // WebClient 호출 체인 목
        whenever(webClient.get()).thenReturn(uriSpec)
        // uri() 호출 시 Function<UriBuilder, URI> 제네릭을 명시적으로 지정
        whenever(uriSpec.uri(any<Function<UriBuilder, URI>>())).thenReturn(headersSpec)
        whenever(headersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(NaverNewsResponse::class.java))
            .thenReturn(Mono.just(newsResponse))
        whenever(responseSpec.bodyToMono(NaverImageResponse::class.java))
            .thenReturn(Mono.just(imageResponse))

        // when
        // service.search 시그니처가 sizeHeight, sizeWidth 파라미터를 추가로 받는 경우,
        // 테스트에서도 동일하게 넘겨야 합니다.
        val result: List<NewsDto> =
            service.search(
                query   = query,
                display = 1
            )

        // then
        assertEquals(1, result.size)
        val dto = result.first()
        assertEquals("테스트 뉴스", dto.title)
        assertEquals("news.example.com", dto.press)
        // dto.image는 nullable String 이므로 안전 호출 혹은 !! 필요
        assertTrue(dto.image!!.contains("thumb.jpg"))
        assertEquals("https://news.example.com/article/1", dto.link)
    }
}