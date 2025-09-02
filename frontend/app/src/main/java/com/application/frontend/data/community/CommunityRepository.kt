package com.application.frontend.data.community

import com.application.frontend.model.CreateCommentRequest
import com.application.frontend.model.Notification
import com.application.frontend.model.NotificationMeta
import com.application.frontend.model.Post

class CommunityRepository(
    private val api: CommunityApi
) {
    suspend fun getFeed(): List<Post> = api.getFeed()

    // 알림 목록
    suspend fun getNotifications(): List<Notification> = api.getNotifications()

    // ViewModel에서 쓰는 메타/읽음 처리 추가
    suspend fun getNotificationMeta(): NotificationMeta = api.getNotificationMeta()
    suspend fun readAllNotifications(): NotificationMeta = api.readAllNotifications()

    // 단건 삭제
    suspend fun deleteNotification(id: Long): NotificationMeta = api.deleteNotification(id)


    // 단건 읽음 처리
    suspend fun readNotification(id: Long): NotificationMeta = api.readNotification(id)

    suspend fun getComments(postId: String) = api.getComments(postId)
    suspend fun createComment(postId: String, body: CreateCommentRequest) = api.createComment(postId, body)
    suspend fun likeComment(commentId: String) = api.likeComment(commentId)
    suspend fun deleteComment(commentId: String) = api.deleteComment(commentId)
}