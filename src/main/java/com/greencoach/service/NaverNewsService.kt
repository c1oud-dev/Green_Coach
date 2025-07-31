package com.greencoach.service

import com.greencoach.model.NaverImageResponse
import com.greencoach.model.NaverNewsItem
import com.greencoach.model.NaverNewsResponse
import com.greencoach.model.NewsDto
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.HtmlUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Service
class NaverNewsService(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${naver.client.id}")     private val clientId: String,
    @Value("\${naver.client.secret}") private val clientSecret: String,
    @Value("\${naver.news.base-url}") private val baseUrl: String
) {
    private val client: WebClient = webClientBuilder
        .baseUrl(baseUrl)
        .defaultHeader("Naver-Client-Id", clientId)
        .defaultHeader("Naver-Client-Secret", clientSecret)
        .build()

    suspend fun search(query: String, display: Int = 5): List<NewsDto> {
        // 1) 뉴스 검색
        val newsResp = client.get()
            .uri { b -> b.path("/v1/search/news.json")
                .queryParam("query", query)
                .queryParam("display", display)
                .build() }
            .retrieve()
            .bodyToMono(NaverNewsResponse::class.java)
            .awaitSingle()

        // 2) 각 뉴스 제목으로 이미지 검색 후 DTO 생성
        return newsResp.items.map { item ->
            // 제목을 그대로 이미지 검색 쿼리로 사용
            val imgResp = client.get()
                .uri { b -> b.path("/v1/search/image.json")
                    .queryParam("query", HtmlUtils.htmlUnescape(item.title))
                    .queryParam("display", 1) // 한 개만
                    .build() }
                .retrieve()
                .bodyToMono(NaverImageResponse::class.java)
                .awaitSingleOrNull()

            // thumbnail 이 없으면 빈 문자열 반환
            val thumbnail = imgResp?.items
                ?.firstOrNull()
                ?.thumbnail
                .orEmpty()

                toDto(item, thumbnail)
        }
    }

    // imageUrl 파라미터 추가
    private fun toDto(item: NaverNewsItem, imageUrl: String): NewsDto {
        val title = HtmlUtils.htmlUnescape(item.title)
        val press = item.originallink
            .substringAfter("://").substringBefore("/")
            .removePrefix("www.")
        val timeAgo = toRelative(item.pubDate)
        return NewsDto(
            title = title,
            press = press,
            timeAgo = timeAgo,
            image   = imageUrl,          // 새로 채워진 썸네일
            link = item.originallink // 혹은 item.link (기사 원문 URL)
        )
    }

    private fun toRelative(pubDate: String): String {
        val fmt = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
        val then = fmt.parse(pubDate) ?: Date()
        val diff = (Date().time - then.time) / 1000
        return when {
            diff < 60    -> "${diff}s ago"
            diff < 3600  -> "${diff/60}m ago"
            else         -> "${diff/3600}h ago"
        }
    }
}