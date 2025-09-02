package com.greencoach.model.community

import jakarta.persistence.*
import java.time.Instant

@Entity @Table(name = "comments")
data class CommentEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val postId: Long,

    @Column(nullable = true)
    val parentId: Long? = null,

    @Column(nullable = false)
    val authorId: Long,

    @Column(nullable = false, length = 2000)
    val content: String,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    val likeCount: Int = 0
)