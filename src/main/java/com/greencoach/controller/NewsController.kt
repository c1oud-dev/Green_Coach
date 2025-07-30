package com.greencoach.controller

import com.greencoach.model.NewsDto
import com.greencoach.service.NaverNewsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/news")
class NewsController(
    private val newsService: NaverNewsService
) {
    @GetMapping
    suspend fun getNews(
        @RequestParam(defaultValue = "분리배출") query: String
    ): List<NewsDto> = newsService.search(query)
}