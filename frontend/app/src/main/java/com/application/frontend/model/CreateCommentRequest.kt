package com.application.frontend.model

data class CreateCommentRequest(
    val content: String,
    val parentId: Long? = null,
    val authorName: String? = null,
    val authorHeadline: String? = null,
    val authorAvatarUrl: String? = null
)