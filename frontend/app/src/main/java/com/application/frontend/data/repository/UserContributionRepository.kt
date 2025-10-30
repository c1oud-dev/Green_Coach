package com.application.frontend.data.repository

import com.application.frontend.model.Comment
import com.application.frontend.model.Post
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class UserContributionRepository @Inject constructor() {

    private val _myPosts = MutableStateFlow<List<MyPost>>(emptyList())
    val myPosts: StateFlow<List<MyPost>> = _myPosts.asStateFlow()

    private val _myReviews = MutableStateFlow<List<MyReview>>(emptyList())
    val myReviews: StateFlow<List<MyReview>> = _myReviews.asStateFlow()

    fun recordPost(post: Post, timestampMillis: Long = System.currentTimeMillis()) {
        val entry = MyPost(
            id = post.id,
            authorName = post.author,
            content = post.content,
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            createdAtMillis = timestampMillis
        )
        _myPosts.update { list ->
            listOf(entry) + list.filterNot { it.id == entry.id }
        }
    }

    fun updatePost(post: Post) {
        _myPosts.update { list ->
            list.map { existing ->
                if (existing.id == post.id) {
                    existing.copy(
                        authorName = post.author,
                        content = post.content,
                        likeCount = post.likeCount,
                        commentCount = post.commentCount
                    )
                } else {
                    existing
                }
            }
        }
    }

    fun updatePostCommentCount(postId: String, commentCount: Int) {
        _myPosts.update { list ->
            list.map { existing ->
                if (existing.id == postId) existing.copy(commentCount = commentCount) else existing
            }
        }
    }

    fun removePost(postId: String) {
        _myPosts.update { list -> list.filterNot { it.id == postId } }
    }

    fun recordReview(comment: Comment, timestampMillis: Long = System.currentTimeMillis()) {
        val entry = MyReview(
            id = comment.id,
            postId = comment.postId,
            content = comment.content,
            createdAtMillis = timestampMillis
        )
        _myReviews.update { list ->
            listOf(entry) + list.filterNot { it.id == entry.id }
        }
    }

    fun removeReview(commentId: String) {
        _myReviews.update { list -> list.filterNot { it.id == commentId } }
    }

    fun clear() {
        _myPosts.value = emptyList()
        _myReviews.value = emptyList()
    }
}

data class MyPost(
    val id: String,
    val authorName: String,
    val content: String,
    val likeCount: Int,
    val commentCount: Int,
    val createdAtMillis: Long
)

data class MyReview(
    val id: String,
    val postId: String,
    val content: String,
    val createdAtMillis: Long
)