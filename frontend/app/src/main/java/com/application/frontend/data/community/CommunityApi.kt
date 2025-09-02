package com.application.frontend.data.community

import com.application.frontend.model.Comment
import com.application.frontend.model.CreateCommentRequest
import com.application.frontend.model.Notification
import com.application.frontend.model.NotificationMeta
import com.application.frontend.model.Post
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CommunityApi {
    @GET("/community/feed")
    suspend fun getFeed(): List<Post>

    @GET("/community/notifications")
    suspend fun getNotifications(): List<Notification>

    @GET("/community/notifications/meta")
    suspend fun getNotificationMeta(): NotificationMeta

    @POST("/community/notifications/read-all")
    suspend fun readAllNotifications(): NotificationMeta

    // 단건 읽음
    @POST("/community/notifications/read")
    suspend fun readNotification(@retrofit2.http.Query("id") id: Long): NotificationMeta


    @GET("/community/posts/{postId}/comments")
    suspend fun getComments(@Path("postId") postId: String): List<Comment>

    @POST("/community/posts/{postId}/comments")
    suspend fun createComment(
        @Path("postId") postId: String,
        @Body body: CreateCommentRequest
    ): Comment

    @POST("/community/comments/{commentId}/like")
    suspend fun likeComment(@Path("commentId") commentId: String): Comment

    @DELETE("/community/comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: String)

    @DELETE("/community/notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: Long): NotificationMeta
}