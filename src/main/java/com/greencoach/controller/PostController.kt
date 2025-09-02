package com.greencoach.controller

import com.greencoach.model.community.CommunityPostDto
import com.greencoach.model.community.CreatePostRequest
import com.greencoach.service.PostService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/community")
class PostController(
    private val postService: PostService
) {
    @GetMapping("/feed")
    fun feed(): ResponseEntity<List<CommunityPostDto>> =
        ResponseEntity.ok(postService.getFeed())

    @PostMapping("/posts")
    fun create(@RequestBody req: CreatePostRequest): ResponseEntity<CommunityPostDto> =
        ResponseEntity.ok(postService.createPost(req))
}