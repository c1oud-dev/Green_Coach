package com.application.frontend.model

data class TogglePostLikeRequest(
    val liked: Boolean,
    val actorId: Long?,
    val actorName: String?
)