package com.application.frontend.model

data class NewsDto(
    val title: String,
    val press: String,
    val timeAgo: String,
    val image: String,   // ① API에서 받은 이미지 URL
    val link: String     // ② 중복 제거 및 카드 클릭 시 사용할 기사 링크
)
