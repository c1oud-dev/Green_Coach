package com.greencoach.model.community

data class TogglePostLikeRequest(
    val liked: Boolean,
    val actorId: Long? = null,
    val actorName: String? = null
)