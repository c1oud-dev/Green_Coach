package com.greencoach.service

import com.greencoach.model.community.CommunityAuthorDto
import com.greencoach.model.community.CommunityPostDto
import com.greencoach.model.community.CreatePostRequest
import com.greencoach.model.community.PostEntity
import com.greencoach.model.community.TogglePostLikeRequest
import com.greencoach.repository.PostRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class PostService(
    private val postRepository: PostRepository,
    private val communityService: CommunityService
) {
    fun getFeed(): List<CommunityPostDto> =
        postRepository.findAll()
            .sortedByDescending { it.createdAt }
            .map { it.toDto() }

    fun createPost(req: CreatePostRequest): CommunityPostDto {
        // PostEntity 저장
        val saved = postRepository.save(
            PostEntity(
                authorId = 0L, // ⚠️ 아직 User 시스템 없음 → 임시 0
                content = req.text ?: "",
                createdAt = Instant.now()
            )
        )

        // DTO로 변환
        return saved.toDto(
            liked = false,
            media = req.media ?: emptyList(),
            authorName = req.authorName,
            authorHeadline = req.authorHeadline,
            authorAvatarUrl = req.authorAvatarUrl
        )
    }

    fun toggleLike(postId: Long, req: TogglePostLikeRequest): CommunityPostDto {
        val entity = postRepository.findById(postId).orElseThrow()
        val delta = if (req.liked) 1 else -1
        val updated = postRepository.save(
            entity.copy(likeCount = (entity.likeCount + delta).coerceAtLeast(0))
        )
        if (req.liked) {
            communityService.notifyLike(
                actorId = req.actorId ?: 0L,
                actorName = req.actorName,
                postId = updated.id,
                targetOwnerId = entity.authorId,
                previewText = updated.content
            )
        }

        return updated.toDto(liked = req.liked)
    }

    // Entity → DTO 변환
    private fun PostEntity.toDto(
        liked: Boolean = false,
        bookmarked: Boolean = false,
        media: List<com.greencoach.model.community.MediaDto> = emptyList(),
        authorName: String? = null,
        authorHeadline: String? = null,
        authorAvatarUrl: String? = null
    ) = CommunityPostDto(
        id = this.id,
        author = CommunityAuthorDto(
            id = this.authorId,
            name = authorName ?: "User${this.authorId}",    // TODO: 사용자 정보 연동 시 교체
            headline = authorHeadline ?: "",
            avatarUrl = authorAvatarUrl
        ),
        createdAt = this.createdAt,
        text = this.content,
        media = media,
        likeCount = this.likeCount,
        commentCount = this.commentCount,
        liked = liked,
        bookmarked = bookmarked
    )
}