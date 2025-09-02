package com.application.frontend.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.application.frontend.ui.screen.community.components.*
import com.application.frontend.viewmodel.CommunityViewModel

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    onNavigateProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLoginDialog by remember { mutableStateOf(false) }

    // 화면 진입 시 배지 상태 동기화
    LaunchedEffect(Unit) {
        viewModel.loadMeta()               // ⬅ 메타(로그인/미확인) 1회 조회
    }

    Scaffold(
        topBar = {
            CommunityTopBar(
                onSearchClick = { /* TODO */ },
                onNotificationClick = { viewModel.toggleSheet() },
                isLoggedIn = uiState.isLoggedIn,
                unreadCount = uiState.unreadCount
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color.White)
        ) {
            // 상단 화이트 영역: 작성 박스 + 구분선
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (uiState.isLoggedIn) {
                    PostComposer(
                        text = uiState.composerText,
                        repliesDisabled = uiState.repliesDisabled,
                        onTextChange = { viewModel.updateComposer(it) },
                        onToggleReplies = { viewModel.toggleReplies() },
                        onPickImages = { viewModel.addMedia(it) },
                        onPickGif = { viewModel.pickGif(it) },
                        onInsertEmoji = { viewModel.insertEmoji(it) },
                        onPostClick = { viewModel.createPost() },
                        isLoggedIn = uiState.isLoggedIn,
                        onRequireLogin = { showLoginDialog = true }
                    )
                } else {
                    // 로그인 안내 문구 (상단 입력 영역과 동일한 여백 유지)
                    Text(
                        text = "로그인 후 이용해주세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            Divider(thickness = 1.dp, color = Color(0xFFE8EEF1)) // 얇은 회색 라인

            // 하단 회색 배경 영역: 피드
            Surface(
                color = Color(0xFFF7F9FA),
                modifier = Modifier.fillMaxSize()
            ) {
                // 항상 리스트 렌더 → 비어 있어도 소개 게시글만 보임
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 12.dp), // ⬅ 좌우 padding 제거
                    verticalArrangement = Arrangement.spacedBy(12.dp) // ⬅ 간격도 살짝 맞춰주기
                ) {
                    // 소개 게시글
                    items(uiState.feed) { post ->
                        PostCard(
                            post = post,
                            onLike = { viewModel.toggleLike(post.id) },
                            onBookmark = { viewModel.toggleBookmark(post.id) },
                            onComment = { viewModel.openComments(post.id) },
                            isOwner = viewModel.isOwner(post),
                            onEdit = { viewModel.editPost(post.id) },
                            onDelete = { viewModel.deletePost(post.id) },
                            onReport = { viewModel.reportPost(post.id) }
                        )
                    }
                }
            }

            // 호출부 바인딩
            if (uiState.isSheetOpen) {
                NotificationSheet(
                    notifications = uiState.notifications,
                    onDismiss = { viewModel.markAllReadAndCloseSheet() },   // 시트 닫기 + 전체 읽음
                    onMarkAllRead = { viewModel.markAllRead() },            // 시트 유지 + 전체 읽음
                    onRead = { id -> viewModel.markNotificationRead(id) },  // 스와이프 → 읽음 처리
                    onDelete = { id -> viewModel.removeNotification(id) }   // 스와이프 → 삭제 처리
                )
            }

            // ★ 댓글 바텀시트
            if (uiState.isCommentSheetOpen) {
                CommentSheet(
                    postAuthor      = uiState.activePostAuthor,
                    comments        = uiState.activeComments,
                    replyTargetId   = uiState.replyTargetCommentId,              // ★ 추가
                    inputText       = uiState.commentInput,
                    onInputChange   = { viewModel.updateCommentInput(it) },
                    onSubmit        = { viewModel.submitComment() },
                    onReply         = { id, username -> viewModel.enterReplyMode(username, id) }, // ★ 댓글 클릭 시 @멘션
                    onDelete        = { viewModel.deleteComment(it) },           // ★ 스와이프/메뉴 삭제
                    onDismiss       = { viewModel.closeComments() },
                    onCancelReply   = { viewModel.cancelReplyMode() }            // ★ X 버튼
                )
            }

            // ★ 로그인 유도 모달
            if (showLoginDialog) {
                AlertDialog(
                    onDismissRequest = { showLoginDialog = false },
                    title = { Text("로그인 후 이용해주세요.") },
                    text  = { Text("글 작성 및 업로드, 댓글/좋아요는 로그인 후 이용할 수 있어요.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showLoginDialog = false
                            onNavigateProfile()                   // ★ Profile 화면으로 이동
                        }) { Text("로그인 하러 가기") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLoginDialog = false }) { Text("닫기") }
                    }
                )
            }

        }
    }
}