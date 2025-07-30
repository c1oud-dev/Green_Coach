package com.greencoach.model

data class NaverNewsItem(
    val title: String,
    val originallink: String,
    val description: String,
    val pubDate: String,
    val thumbnail: String? = null   // API에서 넘어오는 썸네일 URL,
)
