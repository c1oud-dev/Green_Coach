package com.greencoach.controller

import com.greencoach.model.community.CommunityPostDto
import com.greencoach.model.community.TogglePostLikeRequest
import com.greencoach.service.PostService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/community/posts")
class PostController(
    private val postService: PostService
) {
    @PostMapping("/{postId}/like")
    fun toggleLike(
        @PathVariable postId: Long,
        @RequestBody req: TogglePostLikeRequest
    ): ResponseEntity<CommunityPostDto> =
        ResponseEntity.ok(postService.toggleLike(postId, req))
}