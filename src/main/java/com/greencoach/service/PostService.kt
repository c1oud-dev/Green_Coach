package com.greencoach.service

import com.greencoach.model.community.CommunityAuthorDto
import com.greencoach.model.community.CommunityPostDto
import com.greencoach.model.community.CreatePostRequest
import com.greencoach.model.community.PostEntity
import com.greencoach.repository.PostRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class PostService(
    private val postRepository: PostRepository
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
        return CommunityPostDto(
            id = saved.id,
            author = CommunityAuthorDto(
                id = saved.authorId,
                name = req.authorName ?: "Anonymous",
                headline = req.authorHeadline ?: "",
                avatarUrl = req.authorAvatarUrl
            ),
            createdAt = saved.createdAt,
            text = saved.content,
            media = req.media ?: emptyList(),
            likeCount = saved.likeCount,
            commentCount = saved.commentCount,
            liked = false,
            bookmarked = false
        )
    }

    // Entity → DTO 변환
    private fun PostEntity.toDto() = CommunityPostDto(
        id = this.id,
        author = CommunityAuthorDto(
            id = this.authorId,
            name = "User${this.authorId}",    // TODO: 사용자 정보 연동 시 교체
            headline = "",
            avatarUrl = null
        ),
        createdAt = this.createdAt,
        text = this.content,
        media = emptyList(),
        likeCount = this.likeCount,
        commentCount = this.commentCount,
        liked = false,
        bookmarked = false
    )
}