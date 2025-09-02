package com.greencoach.model.community

import java.time.Instant

data class CommunityPostDto(
    val id: Long,
    val author: CommunityAuthorDto,
    val createdAt: Instant,
    val text: String,
    val media: List<MediaDto> = emptyList(),
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val liked: Boolean = false,
    val bookmarked: Boolean = false
)

data class CommunityAuthorDto(
    val id: Long,
    val name: String,
    val headline: String,
    val avatarUrl: String? = null
)

data class MediaDto(
    val url: String,
    val type: String // "image", "gif", "video" 등
)