package com.greencoach.model.community

import java.time.Instant

/** 알림 유형: 실제 이벤트 타입 위주로 단순화 */
enum class NotificationType { LIKE, COMMENT, REPLY, FOLLOW, SYSTEM }

data class CommunityNotificationDto(
    val id: Long,
    val type: NotificationType,
    /** 알림을 유발한 사용자 */
    val actorId: Long,
    val actorName: String? = null,
    /** 어떤 리소스에 대한 알림인지 (게시글/댓글 등) */
    val postId: Long? = null,
    val commentId: Long? = null,
    val replyToName: String? = null,
    /** 생성 시각 & 읽음 여부 */
    val createdAt: Instant = Instant.now(),
    val read: Boolean = false
)

/** 배지 제어용 메타 응답 */
data class CommunityNotificationMetaDto(
    val isLoggedIn: Boolean,
    val unreadCount: Int
)

data class NotificationCta(
    val accept: Boolean = false,
    val decline: Boolean = false,
    val primaryText: String? = null
)