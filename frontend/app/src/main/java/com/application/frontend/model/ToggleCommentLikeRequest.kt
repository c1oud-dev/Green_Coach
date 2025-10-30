package com.application.frontend.model

data class ToggleCommentLikeRequest(
    val liked: Boolean,
    val actorId: Long?,
    val actorName: String?
)