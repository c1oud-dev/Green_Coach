package com.greencoach.model.community

data class ReactionRequest(
    val postId: Long,
    val type: ReactionType,
    val value: Boolean? = null // null이면 toggle 의미
)

enum class ReactionType { LIKE, BOOKMARK }

data class ApiResponse(
    val success: Boolean,
    val message: String
)