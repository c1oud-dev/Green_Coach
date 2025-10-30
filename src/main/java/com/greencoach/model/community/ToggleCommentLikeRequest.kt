package com.greencoach.model.community

data class ToggleCommentLikeRequest(
    val liked: Boolean,
    val actorId: Long? = null,
    val actorName: String? = null
)