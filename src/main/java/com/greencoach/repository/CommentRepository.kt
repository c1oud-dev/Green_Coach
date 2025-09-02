package com.greencoach.repository

import com.greencoach.model.community.CommentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<CommentEntity, Long> {
    fun findByPostIdOrderByCreatedAtAsc(postId: Long): List<CommentEntity>

    // 자식(대댓글) 조회
    fun findByParentId(parentId: Long): List<CommentEntity>
}