package com.greencoach.service

import com.greencoach.model.community.*
import com.greencoach.repository.CommentRepository
import com.greencoach.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class CommentService(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val communityService: CommunityService // 알림용 (좋아요/댓글 발생 시) :contentReference[oaicite:2]{index=2}
) {

    /** 댓글 목록 조회 (부모 아래로 대댓글 묶어서 반환)*/
    fun getComments(postId: Long): List<CommentDto> {
        val all = commentRepository.findByPostIdOrderByCreatedAtAsc(postId)

        // Entity -> DTO(일단 replies 비움)
        val dtoById = all.associate { it.id to it.toDto() }.toMutableMap()

        // parentId별로 묶기 (null = 루트 댓글)
        val byParent = all.groupBy { it.parentId }

        // 루트에 대해 children 붙이기 (1-depth 구조)
        val roots = byParent[null].orEmpty().map { parent ->
            val pDto = dtoById[parent.id]!!
            val children = byParent[parent.id].orEmpty().map { dtoById[it.id]!! }
            pDto.copy(replies = children)
        }
        return roots
    }

    /** 댓글 작성 */
    @Transactional
    fun createComment(postId: Long, req: CreateCommentRequest): CommentDto {
        val saved = commentRepository.save(
            CommentEntity(
                postId = postId,
                parentId = req.parentId,                 // ★ 부모 id 저장
                authorId = 0L, // TODO: 보안 붙이면 실제 사용자 id
                content = req.content,
                createdAt = Instant.now()
            )
        )

        // 게시글 댓글 수 증가
        postRepository.findById(postId).ifPresent {
            postRepository.save(it.copy(commentCount = it.commentCount + 1))
        }

        // 알림 트리거
        communityService.notifyComment(
            actorId = saved.authorId,
            actorName = req.authorName ?: "Anonymous",
            postId = postId,
            commentId = saved.id
        )

        return saved.toDto(
            authorName = req.authorName,
            authorHeadline = req.authorHeadline,
            authorAvatarUrl = req.authorAvatarUrl
        )
    }

    /** 댓글 좋아요 */
    @Transactional
    fun likeComment(commentId: Long): CommentDto {
        val updated = commentRepository.findById(commentId).orElseThrow()
        val liked = updated.copy(likeCount = updated.likeCount + 1)
        return commentRepository.save(liked).toDto()
    }

    /** 댓글 삭제(대댓글 포함) */
    @Transactional
    fun deleteComment(commentId: Long) {
        val target = commentRepository.findById(commentId).orElseThrow()

        // ★ 자식(대댓글) 먼저 삭제 — FK 제약/고아 레코드 방지
        val children = commentRepository.findByParentId(commentId)
        if (children.isNotEmpty()) {
            commentRepository.deleteAll(children)
        }

        // 부모 삭제
        commentRepository.delete(target)

        // ★ 댓글 수 동기화: 부모 + 자식 개수만큼 감소
        val delta = 1 + children.size
        postRepository.findById(target.postId).ifPresent {
            postRepository.save(it.copy(commentCount = (it.commentCount - delta).coerceAtLeast(0)))
        }
    }


    // Entity → DTO 변환
    private fun CommentEntity.toDto(
        authorName: String? = null,
        authorHeadline: String? = null,
        authorAvatarUrl: String? = null
    ) = CommentDto(
        id = this.id,
        postId = this.postId,
        parentId = this.parentId,
        author = authorName ?: "User${this.authorId}",  // 👈 문자열
        authorId = this.authorId,                        // 👈 별도 필드
        content = this.content,
        createdAt = this.createdAt,
        timeText = this.createdAt.toString(),
        likeCount = this.likeCount
    )
}