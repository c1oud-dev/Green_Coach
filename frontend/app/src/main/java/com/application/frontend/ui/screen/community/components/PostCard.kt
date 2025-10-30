package com.application.frontend.ui.screen.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.application.frontend.R
import com.application.frontend.model.Post

@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit,
    onBookmark: () -> Unit,
    onComment: () -> Unit,
    showBookmark: Boolean,
    isOwner: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onReport: () -> Unit
) {
    val menuOpen = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // 그림자 제거
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                if (post.id == "intro-post") {
                    Box(
                        modifier = Modifier
                            .size(40.dp) // 프로필 영역 전체 크기
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFE5E8EB), CircleShape) // 회색 테두리
                            .background(Color.White),                     // 흰색 배경
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_leaf_logo),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp), // 로고는 내부에 작게
                            tint = Color.Unspecified         // 원본 색 유지
                        )
                    }
                } else {
                    // 사용자 글: 기존 자리표시자(프로필 이미지 준비되면 여기 교체)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(post.author, style = MaterialTheme.typography.titleMedium)
                    Row {
                        Text(
                            post.authorTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (post.timeText.isNotEmpty()) {
                            Text(
                                " • ${post.timeText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Box {
                    IconButton(onClick = { menuOpen.value = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "more")
                    }
                    DropdownMenu(expanded = menuOpen.value, onDismissRequest = { menuOpen.value = false }) {
                        if (isOwner) {
                            DropdownMenuItem(
                                text = { Text("수정하기") },
                                onClick = { menuOpen.value = false; onEdit() }
                            )
                            DropdownMenuItem(
                                text = { Text("삭제하기") },
                                onClick = { menuOpen.value = false; onDelete() }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("신고하기") },
                                onClick = { menuOpen.value = false; onReport() }
                            )
                        }
                    }
                }
            }

            // 본문
            if (post.content.isNotBlank()) {
                Text(post.content, style = MaterialTheme.typography.bodyMedium)
            }

            // 이미지
            ImageGrid(post.mediaUrls)

            // 리액션/카운트
            ReactionRow(
                likeCount = post.likeCount,
                commentCount = post.commentCount,
                liked = post.liked,
                bookmarked = post.bookmarked,
                showBookmark = showBookmark,
                onLike = onLike,
                onBookmark = onBookmark,
                onComment = onComment
            )
        }
    }
}