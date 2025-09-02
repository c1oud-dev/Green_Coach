package com.greencoach.model.community

import java.time.Instant

data class CommentDto(
    val id: Long,
    val postId: Long,
    val parentId: Long?,
    val author: String,
    val authorId: Long,
    val content: String,
    val createdAt: Instant,
    val timeText: String,
    val likeCount: Int,
    val liked: Boolean = false,
    val isOwner: Boolean = false,
    val replies: List<CommentDto> = emptyList(),
)