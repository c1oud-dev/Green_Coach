package com.application.frontend.model

data class Post(
    val id: String,
    val author: String,
    val authorTitle: String = "",     // 예: "Bachelor of CS | Web Developer"
    val timeText: String = "",        // 예: "20 min"
    val content: String,
    val mediaUrls: List<String> = emptyList(), // 이미지 0~N
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val liked: Boolean = false,
    val bookmarked: Boolean = false,
    val authorId: String = ""                // 글 소유자 식별
)