package com.greencoach.controller

import com.greencoach.model.community.CommentDto
import com.greencoach.model.community.CreateCommentRequest
import com.greencoach.model.community.ToggleCommentLikeRequest
import com.greencoach.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/community")
class CommentController(
    private val commentService: CommentService
) {
    /** 댓글 목록 조회 */
    @GetMapping("/posts/{postId}/comments")
    fun list(@PathVariable("postId") postIdRaw: String): ResponseEntity<List<CommentDto>> =
        ResponseEntity.ok(commentService.getComments(resolvePostId(postIdRaw)))

    /** 댓글 작성 */
    @PostMapping("/posts/{postId}/comments")
    fun create(
        @PathVariable("postId") postIdRaw: String,
        @RequestBody req: CreateCommentRequest
    ): ResponseEntity<CommentDto> =
        ResponseEntity.ok(commentService.createComment(resolvePostId(postIdRaw), req))

    /** 댓글 좋아요 */
    @PostMapping("/comments/{commentId}/like")
    fun like(
        @PathVariable commentId: Long,
        @RequestBody req: ToggleCommentLikeRequest
    ): ResponseEntity<CommentDto> =
        ResponseEntity.ok(commentService.likeComment(commentId, req))

    /** 댓글 삭제 */
    @DeleteMapping("/comments/{commentId}")
    fun delete(@PathVariable commentId: Long): ResponseEntity<Void> {
        commentService.deleteComment(commentId)
        return ResponseEntity.noContent().build() // ★ 204 No Content
    }

    // intro-post 같은 슬러그를 숫자 ID로 매핑
    private fun resolvePostId(raw: String): Long {
        return raw.toLongOrNull() ?: when (raw) {
            "intro-post" -> 1L  // ★ 고정 ID로 매핑(원하면 상수/설정으로 빼세요)
            else -> throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid postId: $raw"
            )
        }
    }
}