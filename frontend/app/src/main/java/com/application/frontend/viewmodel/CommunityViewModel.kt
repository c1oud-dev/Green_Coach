package com.application.frontend.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.data.community.CommunityRepository
import com.application.frontend.model.Comment
import com.application.frontend.model.CreateCommentRequest
import com.application.frontend.model.Notification
import com.application.frontend.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommunityUiState(
    val composerText: String = "",
    val feed: List<Post> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val isSheetOpen: Boolean = false,
    val isLoggedIn: Boolean = false,
    val unreadCount: Int = 0,
    val repliesDisabled: Boolean = false,
    val selectedMedia: List<Uri> = emptyList(),   // 이미지/동영상 URI
    val selectedGifUrl: String? = null,            // 선택된 GIF (있으면 미디어에 포함)

    // ★ 댓글 시트 관련 상태
    val isCommentSheetOpen: Boolean = false,
    val activePostIdForComments: String? = null,
    val activePostAuthor: String? = null,
    val activeComments: List<Comment> = emptyList(),
    val commentInput: String = "",
    val replyTargetUsername: String? = null,
    val replyTargetCommentId: String? = null,
    val replyPrefix: String? = null,                // 실제로 붙인 '@username ' 전체 문자열
    val replyParentIdLong: Long? = null,
)

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val repository: CommunityRepository
) : ViewModel() {

    private val currentUserId = "Green Coach"    // TODO: 로그인 붙을 때 실제 사용자 id로 대체(로그인 전 임시 표시명 고정)
    private val currentUserIdLong = 0L     // ★ 추가: 서버의 authorId(0)와 동일

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState

    /** 최초 진입/재진입 시 배지 상태 동기화 */
    fun loadMeta() {
        viewModelScope.launch {
            val meta = repository.getNotificationMeta()          // ← 백엔드 /community/notifications/meta
            setAuthAndUnread(meta.isLoggedIn, meta.unreadCount)
        }
    }

    /** 시트 열고 목록만 로드(읽음은 하지 않음) */
    fun toggleSheet() {
        val nowOpen = !_uiState.value.isSheetOpen
        _uiState.value = _uiState.value.copy(isSheetOpen = nowOpen)
        if (nowOpen) {
            loadNotifications()
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            val list = repository.getNotifications()              // ← /community/notifications
            _uiState.value = _uiState.value.copy(notifications = list)
        }
    }

    /** 시트 닫으면서 전체 읽음 처리 + 배지 업데이트 */
    fun markAllReadAndCloseSheet() {
        viewModelScope.launch {
            val meta = repository.readAllNotifications()          // ← /community/notifications/read-all
            _uiState.value = _uiState.value.copy(
                isSheetOpen = false,
                isLoggedIn = meta.isLoggedIn,
                unreadCount = meta.unreadCount
            )
        }
    }

    /** 시트는 유지한 채 전체 읽음 */
    fun markAllRead() {
        viewModelScope.launch {
            val meta = repository.readAllNotifications()
            _uiState.value = _uiState.value.copy(
                // 목록의 read 플래그도 전부 true 로 동기화
                notifications = _uiState.value.notifications.map { it.copy(read = true) },
                isLoggedIn = meta.isLoggedIn,
                unreadCount = meta.unreadCount
            )
        }
    }

    /** 단건 읽음 */
    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            val meta = repository.readNotification(id)
            _uiState.value = _uiState.value.copy(
                notifications = _uiState.value.notifications.map {
                    if (it.id == id) it.copy(read = true) else it
                },
                unreadCount = meta.unreadCount
            )
        }
    }

    /** 단건 삭제(로컬) */
    fun removeNotification(id: Long) {
        viewModelScope.launch {
            // 1) 서버에 영구 삭제
            val meta = repository.deleteNotification(id)

            // 2) UI에서도 즉시 제거
            val wasUnread = _uiState.value.notifications.any { it.id == id && !it.read }
            _uiState.value = _uiState.value.copy(
                notifications = _uiState.value.notifications.filterNot { it.id == id },
                // 서버 메타가 오면 그 값을 신뢰, 없으면 로컬 계산값 사용
                unreadCount = meta.unreadCount.takeIf { it >= 0 }
                    ?: (_uiState.value.unreadCount - if (wasUnread) 1 else 0).coerceAtLeast(0)
            )
        }
    }

    /** 로그인/배지 상태 갱신용 헬퍼 */
    fun setAuthAndUnread(isLoggedIn: Boolean, unreadCount: Int) {
        _uiState.value = _uiState.value.copy(
            isLoggedIn = isLoggedIn,
            unreadCount = unreadCount.coerceAtLeast(0)
        )
    }

    fun updateComposer(text: String) {
        _uiState.value = _uiState.value.copy(composerText = text)
    }

    init {
        // 소개 게시글을 feed 맨 앞에 넣어둔다
        val intro = getIntroPost()
        _uiState.value = _uiState.value.copy(feed = listOf(intro))
    }

    // 앱 첫 화면에 항상 노출될 '소개 게시글'
    fun getIntroPost(): Post = Post(
        id = "intro-post",
        author = "Green Coach",
        authorTitle = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")),
        timeText = "20 min", // 디자인 샘플 값 (원하면 상대시간으로 교체)
        content = "\uD83C\uDF31 함께 만드는 친환경 커뮤니티  \n" +
                "안녕하세요, Green Coach입니다.\n\n" +
                "\uD83D\uDCA1 첫 글을 작성해 보세요.\n" +
                " - 게시글을 작성해 자신의 환경 실천을 공유하고,\n" +
                " - 다른 사람의 글에 댓글과 좋아요를 남길 수 있어요.\n" +
                " - 사진이나 GIF, 이모지를 활용해 자유롭게 표현해 보세요.\n\n" +
                "당신의 글이 누군가의 새로운 시작이 될 수 있습니다 \uD83D\uDC9A",
        likeCount = 0,
        commentCount = 0,
        authorId = "intro"            // ⬅ 소개글은 소유자 아님 처리
    )

    // 첨부/댓글허용 반영 (백엔드 붙기 전 UI용)
    fun createPost() {
        val newPost = Post(
            id = System.currentTimeMillis().toString(),
            author = "Me",
            authorTitle = "",
            timeText = "now",
            content = _uiState.value.composerText.trim(),
            mediaUrls = emptyList(),
            likeCount = 0,
            commentCount = 0,
            authorId = currentUserId          // ⬅ 내 글 표시
        )
        if (newPost.content.isNotEmpty()) {
            val currentFeed = _uiState.value.feed
            val intro = currentFeed.lastOrNull() // intro는 항상 마지막
            val withoutIntro = if (intro?.id == "intro-post") currentFeed.dropLast(1) else currentFeed

            _uiState.value = _uiState.value.copy(
                feed = listOf(newPost) + withoutIntro + listOfNotNull(intro),
                composerText = ""
            )
        }
    }


    // 소유자 판별 & 메뉴 액션 핸들러
    fun isOwner(post: Post): Boolean = post.authorId == currentUserId

    fun editPost(postId: String) {
        // 간단 버전: 내용만 composer로 불러오고 포커싱(수정 모드는 필요 시 확장)
        val target = _uiState.value.feed.firstOrNull { it.id == postId } ?: return
        _uiState.value = _uiState.value.copy(composerText = target.content)
        // TODO: editingPostId 등을 도입해 Post 버튼을 'Update'로 바꾸는 확장 가능
    }

    fun deletePost(postId: String) {
        val cur = _uiState.value.feed
        val after = cur.filterNot { it.id == postId }
        _uiState.value = _uiState.value.copy(feed = after)
    }

    fun reportPost(postId: String) {
        // TODO: 신고 API 연동 예정. 우선 로컬 토스트/스낵바 처리만 계획
    }

    // 댓글 수 증가 로직
    fun addComment(postId: String) {
        val cur = _uiState.value.feed
        val updated = cur.map { p ->
            if (p.id == postId) p.copy(commentCount = (p.commentCount + 1)) else p
            }
        _uiState.value = _uiState.value.copy(feed = updated)
    }

    fun toggleLike(postId: String) {
        _uiState.value = _uiState.value.copy(
            feed = _uiState.value.feed.map { p ->
                if (p.id == postId) {
                    val nowLiked = !p.liked
                    p.copy(
                        liked = nowLiked,
                        likeCount = (p.likeCount + if (nowLiked) 1 else -1).coerceAtLeast(0)
                    )
                } else p
            }
        )
    }

    fun toggleBookmark(postId: String) {
        _uiState.value = _uiState.value.copy(
            feed = _uiState.value.feed.map {
                if (it.id == postId) it.copy(bookmarked = !it.bookmarked) else it
            }
        )
    }

    // 헬퍼 추가
    fun toggleReplies() {
        _uiState.value = _uiState.value.copy(repliesDisabled = !_uiState.value.repliesDisabled)
    }
    fun addMedia(uris: List<Uri>) {
        if (uris.isEmpty()) return
        _uiState.value = _uiState.value.copy(selectedMedia = _uiState.value.selectedMedia + uris)
    }
    fun pickGif(url: String) {
        _uiState.value = _uiState.value.copy(selectedGifUrl = url)
    }
    fun insertEmoji(emoji: String) {
        _uiState.value = _uiState.value.copy(composerText = _uiState.value.composerText + emoji)
    }

    // 루트 + 모든 대댓글을 재귀로 합산
    private fun totalComments(list: List<Comment>): Int {
        fun sumAll(cs: List<Comment>): Int =
            cs.sumOf { 1 + sumAll(it.replies) }
        return sumAll(list)
    }


    // ★ 댓글 시트 열기
    fun openComments(postId: String) {
        val post = _uiState.value.feed.firstOrNull { it.id == postId }
        _uiState.value = _uiState.value.copy(
            isCommentSheetOpen = true,
            activePostIdForComments = postId,
            activePostAuthor = post?.author ?: "",
            // ▼ 이전에 남아 있던 “답글 모드” 흔적 싹 초기화
            replyTargetUsername = null,
            replyTargetCommentId = null,
            replyPrefix = null,
            replyParentIdLong = null
        )
        loadComments(postId)
    }

    // ★ 댓글 시트 닫기
    fun closeComments() {
        _uiState.value = _uiState.value.copy(
            isCommentSheetOpen = false,
            activePostIdForComments = null,
            activePostAuthor = null,
            activeComments = emptyList(),
            commentInput = ""
        )
    }

    // ★ 댓글 로드(실서버 연동)
    fun loadComments(postId: String) {
        viewModelScope.launch {
            val raw = repository.getComments(postId)
            val list = markOwnership(raw)              // ★ 소유자 플래그 채우기
            val count = totalComments(list)

            _uiState.value = _uiState.value.copy(
                activeComments = list,
                feed = _uiState.value.feed.map { p ->
                    if (p.id == postId) p.copy(commentCount = count) else p
                }
            )
        }
    }

    // ★ 입력 업데이트
    fun updateCommentInput(text: String) {
        _uiState.value = _uiState.value.copy(commentInput = text)
    }

    /** 댓글 클릭 → 답글 모드 진입, 입력창에 @username 프리픽스 */
    fun enterReplyMode(username: String, commentId: String) {
        val newPrefix = "@$username, "
        val cur = _uiState.value.commentInput
        val old = _uiState.value.replyPrefix
        val base = if (old != null && cur.startsWith(old)) cur.removePrefix(old) else cur

        val parentIdLong = commentId.filter { it.isDigit() }.toLongOrNull()

        _uiState.value = _uiState.value.copy(
            replyTargetUsername = username,
            replyTargetCommentId = commentId,
            replyPrefix = newPrefix,
            replyParentIdLong = parentIdLong,
            commentInput = if (base.startsWith(newPrefix)) base else newPrefix + base
        )
    }

    /** 답글 모드 취소 (프리픽스 제거) */
    fun cancelReplyMode() {
        val prefix = _uiState.value.replyPrefix
        val cur = _uiState.value.commentInput
        val cleared = if (prefix != null && cur.startsWith(prefix)) cur.removePrefix(prefix) else cur

        _uiState.value = _uiState.value.copy(
            replyTargetUsername = null,
            replyTargetCommentId = null,
            replyPrefix = null,
            replyParentIdLong = null,
            commentInput = cleared
        )
    }

    /** 댓글 등록(로컬 반영) — 답글 모드면 @멘션 유지된 상태로 전송됨 */
    fun submitComment() {
        val raw = _uiState.value.commentInput
        val prefix = _uiState.value.replyPrefix
        // 화면에는 @닉네임, 을 남겨도 서버 전송 본문에서는 제거
        val text = raw.trim()
        val contentToSend =
            if (!prefix.isNullOrEmpty() && text.startsWith(prefix)) text.removePrefix(prefix).trim()
            else text
        if (contentToSend.isEmpty()) return

        val postId = _uiState.value.activePostIdForComments ?: return

        // 1) parentId 안전 변환: 숫자만 추려 Long으로 변환(로컬 id나 접두어가 섞여도 방지)
        val replyToIdRaw = _uiState.value.replyTargetCommentId
        val parentIdLong: Long? =
            _uiState.value.replyParentIdLong
                ?: _uiState.value.replyTargetCommentId?.toLongOrNull()
                ?: _uiState.value.replyTargetCommentId?.filter { it.isDigit() }?.toLongOrNull()

        println("submitComment parentId=$parentIdLong, postId=$postId, content='$contentToSend'")

        viewModelScope.launch {
            // 2) 서버에 저장 (parentId가 null이 아니면 대댓글)
            runCatching {
                repository.createComment(
                    postId = postId,
                    body = CreateCommentRequest(
                        content = contentToSend,
                        parentId = parentIdLong,
                        authorName = "Green Coach"
                    )
                )
            }.onSuccess {
                // 3) 서버에서 목록 재조회 → 부모 아래 정확히 표시
                loadComments(postId)

                // 4) 입력/상태 초기화
                _uiState.value = _uiState.value.copy(
                    commentInput = "",
                    replyTargetUsername = null,
                    replyTargetCommentId = null,
                    replyPrefix = null,
                    replyParentIdLong = null
                )

            }.onFailure { e ->
                // TODO: 스낵바로 바꿔도 됨
                println("createComment failed: ${e.message}")
            }
        }
    }

    /** 댓글 삭제 */
    fun deleteComment(commentId: String) {
        val postId = _uiState.value.activePostIdForComments ?: return
        viewModelScope.launch {
            runCatching { repository.deleteComment(commentId) }
            .onSuccess {
                // 서버 상태 기준으로 다시 가져오며 카운트도 동기화
                loadComments(postId)
            }
        }
    }


    // ★ 좋아요 토글
    fun likeComment(commentId: String) {
        _uiState.value = _uiState.value.copy(
            activeComments = _uiState.value.activeComments.map { c ->
                if (c.id == commentId) c.copy(
                    liked = !c.liked,
                    likeCount = (c.likeCount + if (!c.liked) 1 else -1).coerceAtLeast(0)
                )
                else c.copy(replies = c.replies.map { r ->
                    if (r.id == commentId) r.copy(
                        liked = !r.liked,
                        likeCount = (r.likeCount + if (!r.liked) 1 else -1).coerceAtLeast(0)
                    ) else r
                })
            }
        )
    }

    // 댓글/대댓글의 isOwner를 currentUserId("me")로 판별해 채운다.
    private fun markOwnership(list: List<Comment>): List<Comment> {
        fun mark(c: Comment): Comment {
            val owned = when {
                c.authorId != null && c.authorId == currentUserIdLong -> true
                !c.author.isNullOrBlank() && c.author.equals(currentUserId, ignoreCase = true) -> true
                else -> false
            }
            return c.copy(
                isOwner = owned,
                replies = c.replies.map(::mark)
            )
        }
        return list.map(::mark)
    }

}