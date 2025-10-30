package com.application.frontend.model

data class SavedPost(
    val id: String,
    val authorName: String,
    val authorHeadline: String = "",
    val content: String,
    val likeCount: Int,
    val commentCount: Int,
    val savedAt: Long,
    val mediaPreviewUrl: String? = null
)