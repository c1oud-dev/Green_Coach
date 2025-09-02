package com.application.frontend.model

data class Comment(
    val id: String,
    val postId: String,
    val parentId: Long? = null,
    val authorId: Long? = null,
    val author: String,
    val content: String,
    val timeText: String,
    val liked: Boolean = false,
    val likeCount: Int = 0,
    val isOwner: Boolean = false,
    val replies: List<Comment> = emptyList()
)