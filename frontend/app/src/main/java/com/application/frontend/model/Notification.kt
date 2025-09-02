package com.application.frontend.model

import java.time.Instant

/** 백엔드의 NotificationType 과 동일하게 맞춤 */
enum class NotificationType {
    LIKE, COMMENT, REPLY, FOLLOW, SYSTEM
}

/**
 * 백엔드 DTO 매핑:
 * id: Long,
 * type: NotificationType,
 * actorId: Long,
 * actorName: String?,
 * postId: Long?,
 * commentId: Long?,
 * createdAt: Instant,
 * read: Boolean
 */
data class Notification(
    val id: Long,
    val type: NotificationType,
    val actorId: Long,
    val actorName: String? = null,
    val postId: Long? = null,
    val commentId: Long? = null,
    val replyToName: String? = null,
    val createdAt: Instant,   // 상대시간 표시에 사용 (e.g., "8분 전")
    val read: Boolean = false
)