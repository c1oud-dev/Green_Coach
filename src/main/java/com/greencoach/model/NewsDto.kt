package com.greencoach.model

data class NewsDto(
    val title: String,
    val press: String,
    val timeAgo: String,
    val image: String?,
    val link: String
)
