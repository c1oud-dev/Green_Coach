package com.application.frontend.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                onSearchClick = { viewModel.setSearchQuery(uiState.searchQuery.trim()) }, // ▼ 클릭 시 현재 입력값 확정/재적용
                onNotificationClick = { viewModel.toggleSheet() },
                isLoggedIn = uiState.isLoggedIn,
                unreadCount = uiState.unreadCount,
                onQueryChange = viewModel::setSearchQuery   // ▼ 추가: 입력 → 검색 적용
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
                            onLike = {
                                if (uiState.isLoggedIn) {
                                    viewModel.toggleLike(post.id)
                                } else {
                                    showLoginDialog = true
                                }
                            },
                            onBookmark = {
                                if (uiState.isLoggedIn) {
                                    viewModel.toggleBookmark(post.id)
                                } else {
                                    showLoginDialog = true
                                }
                            },
                            onComment = {
                                if (uiState.isLoggedIn) {
                                    viewModel.openComments(post.id)
                                } else {
                                    showLoginDialog = true
                                }
                            },
                            showBookmark = uiState.isLoggedIn,
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
                BasicAlertDialog(
                    onDismissRequest = { showLoginDialog = false }
                ) {
                    // 카드 스타일 컨테이너
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        tonalElevation = 6.dp,
                        shadowElevation = 12.dp,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        color = Color.White
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(min = 280.dp, max = 360.dp)
                                .padding(horizontal = 20.dp, vertical = 18.dp)
                        ) {
                            // 아이콘 + 타이틀
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(25.dp),
                                    tint = Color(0xFF008080)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "로그인이 필요합니다",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 20.sp
                                    )
                                )
                            }

                            Spacer(Modifier.height(15.dp))
                            Text(
                                text = "좋아요/댓글 기능을 사용하려면 로그인해 주세요.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                            ) {
                                OutlinedButton(
                                    onClick = { showLoginDialog = false },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF008080)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF008080))
                                ) { Text("나중에") }

                                Button(
                                    onClick = {
                                        showLoginDialog = false
                                        onNavigateProfile() // 기존 내비 호출 유지
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF008080),        // 버튼 배경: #008080
                                        contentColor = Color.White                 // 가독성 위해 글씨는 흰색
                                    )
                                ) { Text("로그인하러 가기") }
                            }
                        }
                    }
                }
            }

        }
    }
}