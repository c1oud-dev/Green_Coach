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
    fun notifyLike(actorId: Long, actorName: String?, postId: Long) {
        notifications += CommunityNotificationDto(
            id = seq.incrementAndGet(),
            type = NotificationType.LIKE,
            actorId = actorId,
            actorName = actorName,
            postId = postId,
            createdAt = Instant.now()
        )
    }

    /** 댓글 알림 */
    fun notifyComment(actorId: Long, actorName: String?, postId: Long, commentId: Long) {
        notifications += CommunityNotificationDto(
            id = seq.incrementAndGet(),
            type = NotificationType.COMMENT,
            actorId = actorId,
            actorName = actorName,
            postId = postId,
            commentId = commentId,
            createdAt = Instant.now()
        )
    }

    /** 답글 알림 */
    /** 답글 알림 */
    fun notifyReply(
        actorId: Long,
        actorName: String?,
        postId: Long,
        commentId: Long,
        replyToName: String?    // ⬅ 대상 사용자 이름 추가
    ) {
        notifications += CommunityNotificationDto(
            id = seq.incrementAndGet(),
            type = NotificationType.REPLY,
            actorId = actorId,
            actorName = actorName,
            postId = postId,
            commentId = commentId,
            replyToName = replyToName,   // ⬅ 여기 채움
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