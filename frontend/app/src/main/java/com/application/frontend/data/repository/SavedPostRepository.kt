package com.application.frontend.data.repository

import com.application.frontend.model.Post
import com.application.frontend.model.SavedPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedPostRepository @Inject constructor() {

    private val _savedPosts = MutableStateFlow<List<SavedPost>>(emptyList())
    val savedPosts: StateFlow<List<SavedPost>> = _savedPosts.asStateFlow()

    fun isSaved(postId: String): Boolean = _savedPosts.value.any { it.id == postId }

    fun save(post: Post) {
        val entry = post.toSavedPost()
        _savedPosts.update { list ->
            listOf(entry) + list.filterNot { it.id == post.id }
        }
    }

    fun update(post: Post) {
        _savedPosts.update { list ->
            val existing = list.firstOrNull { it.id == post.id } ?: return@update list
            val updated = existing.copy(
                authorName = post.author,
                authorHeadline = post.authorTitle,
                content = post.content,
                likeCount = post.likeCount,
                commentCount = post.commentCount,
                mediaPreviewUrl = post.mediaUrls.firstOrNull()
            )
            list.map { if (it.id == post.id) updated else it }
        }
    }

    fun remove(postId: String) {
        _savedPosts.update { list -> list.filterNot { it.id == postId } }
    }

    fun clear() {
        _savedPosts.value = emptyList()
    }

    private fun Post.toSavedPost(): SavedPost =
        SavedPost(
            id = this.id,
            authorName = this.author,
            authorHeadline = this.authorTitle,
            content = this.content,
            likeCount = this.likeCount,
            commentCount = this.commentCount,
            savedAt = System.currentTimeMillis(),
            mediaPreviewUrl = this.mediaUrls.firstOrNull()
        )
}