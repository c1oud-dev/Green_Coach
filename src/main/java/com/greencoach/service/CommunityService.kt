package com.greencoach.service

import com.greencoach.model.community.CommunityNotificationDto
import com.greencoach.model.community.CommunityNotificationMetaDto
import com.greencoach.model.community.NotificationType
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

@Service
class CommunityService {

    private val seq = AtomicLong(0L)
    private val notifications = mutableListOf<CommunityNotificationDto>()

    /** 알림 목록 조회 (최신순) */
    fun getNotifications(): List<CommunityNotificationDto> =
        notifications.sortedByDescending { it.createdAt }

    /** 미확인 개수 */
    private fun unreadCount(): Int = notifications.count { !it.read }

    /** 메타 제공: 로그인 여부는 보안 연동 전까지 임시 파라미터 */
    fun getNotificationMeta(isLoggedIn: Boolean = true) =
        CommunityNotificationMetaDto(isLoggedIn = isLoggedIn, unreadCount = unreadCount())

    /** 전체 읽음 처리 */
    fun readAllNotifications(isLoggedIn: Boolean = true): CommunityNotificationMetaDto {
        for (i in notifications.indices) {
            if (!notifications[i].read) {
                notifications[i] = notifications[i].copy(read = true)
            }
        }
        return CommunityNotificationMetaDto(isLoggedIn = isLoggedIn, unreadCount = 0)
    }

    /** 단건 읽음 처리 */
    fun readNotification(id: Long, isLoggedIn: Boolean = true): CommunityNotificationMetaDto {
        val idx = notifications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            notifications[idx] = notifications[idx].copy(read = true)
        }
        return CommunityNotificationMetaDto(isLoggedIn = isLoggedIn, unreadCount = unreadCount())
    }

    // ──────────────────────────────────────────────────────────────
    // 알림 생성 메서드들 – 실제 도메인 로직(좋아요/댓글/답글 발생 시 호출)
    // ──────────────────────────────────────────────────────────────

    /** 좋아요 알림 */
    fun notifyLike(
        actorId: Long,
        actorName: String?,
        postId: Long,
        targetOwnerId: Long?,
        commentId: Long? = null,
        previewText: String? = null
    ) {
        if (targetOwnerId != null && targetOwnerId == actorId) return
        notifications += CommunityNotificationDto(
            id = seq.incrementAndGet(),
            type = NotificationType.LIKE,
            actorId = actorId,
            actorName = actorName,
            postId = postId,
            commentId = commentId,
            previewText = truncatePreview(previewText),
            createdAt = Instant.now()
        )
    }

    /** 댓글 알림 */
    fun notifyComment(
        actorId: Long,
        actorName: String?,
        postId: Long,
        commentId: Long,
        targetOwnerId: Long?,
        previewText: String? = null
    ) {
        if (targetOwnerId != null && targetOwnerId == actorId) return
        notifications += CommunityNotificationDto(
            id = seq.incrementAndGet(),
            type = NotificationType.COMMENT,
            actorId = actorId,
            actorName = actorName,
            postId = postId,
            commentId = commentId,
            previewText = truncatePreview(previewText),
            createdAt = Instant.now()
        )
    }

    /** 답글 알림 */
    fun notifyReply(
        actorId: Long,
        actorName: String?,
        postId: Long,
        commentId: Long,
        replyToName: String?,
        targetOwnerId: Long?,
        previewText: String? = null
    ) {
        if (targetOwnerId != null && targetOwnerId == actorId) return
        notifications += CommunityNotificationDto(
            id = seq.incrementAndGet(),
            type = NotificationType.REPLY,
            actorId = actorId,
            actorName = actorName,
            postId = postId,
            commentId = commentId,
            replyToName = replyToName,
            previewText = truncatePreview(previewText),
            createdAt = Instant.now()
        )
    }

    /** 단건 삭제 */
    fun deleteNotification(id: Long, isLoggedIn: Boolean = true): CommunityNotificationMetaDto {
        val idx = notifications.indexOfFirst { it.id == id }
        if (idx >= 0) notifications.removeAt(idx)
        return CommunityNotificationMetaDto(isLoggedIn = isLoggedIn, unreadCount = unreadCount())
    }
}

private fun truncatePreview(text: String?, maxLength: Int = 160): String? {
    val trimmed = text?.trim().orEmpty()
    if (trimmed.isBlank()) return null
    return if (trimmed.length <= maxLength) trimmed else trimmed.take(maxLength).trimEnd() + "…"
}