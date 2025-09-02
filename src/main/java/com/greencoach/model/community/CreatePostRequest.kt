package com.greencoach.model.community

data class CreatePostRequest(
    val text: String?,
    val media: List<MediaDto>?,
    val authorName: String? = null,
    val authorHeadline: String? = null,
    val authorAvatarUrl: String? = null
)