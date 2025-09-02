package com.application.frontend.ui.screen.community.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.application.frontend.model.Comment

private val CommentInputBarHeight = 72.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSheet(
    postAuthor: String?,
    comments: List<Comment>,
    replyTargetId: String?,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onReply: (id: String, username: String) -> Unit,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit,
    onCancelReply: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White           // ★ 바텀 시트 배경 흰색
    ) {
        Box(
            Modifier
                .fillMaxWidth()
        ) {

            // ── 위: 헤더 + 리스트 (입력 바 높이만큼 아래 패딩을 줘서 겹침 방지) ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(bottom = CommentInputBarHeight)   // ★ 입력 바 공간 확보
            ) {
                // Header
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 6.dp)
                ) {
                    // 댓글 총합 = 루트 + 모든 대댓글
                    val totalCount = remember(comments) {
                        fun countAll(list: List<Comment>): Int =
                            list.sumOf { 1 + countAll(it.replies) }   // 재귀로 안전 처리(다단계도 OK)
                        countAll(comments)
                    }

                    Text(
                        text = "$totalCount COMMENTS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!postAuthor.isNullOrBlank()) {
                        Text(
                            text = "게시자: $postAuthor",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider(color = Color(0xFFE5E8EC))

                // 리스트
                LazyColumn(
                    contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(
                        items = comments,
                        key = { c -> "${c.id}-${c.timeText}-${c.content.hashCode()}" }
                    ) { c ->
                        CommentItem(
                            comment = c,
                            isReplyTarget = (c.id == replyTargetId),
                            replyTargetId = replyTargetId,
                            onRowReply = onReply,
                            onDelete = onDelete,
                            onCancelReply = onCancelReply
                        )
                    }
                }
            }

            // ── 아래: 입력 바 (시트 하단 '고정') ──
            Surface(
                color = Color.White,
                tonalElevation = 1.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)          // ★ 항상 바텀시트의 맨 아래
                    .fillMaxWidth()
                    .heightIn(min = CommentInputBarHeight)
                    .windowInsetsPadding(WindowInsets.ime)   // 키보드와 함께 상승
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = onInputChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("comment") },           // ← placeholder 변경
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),           // ← 라운드 + 외곽선
                        trailingIcon = {                             // ← 입력 박스 내부의 보내기 버튼
                            IconButton(
                                onClick = onSubmit,
                                enabled = inputText.isNotBlank()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Send,
                                    contentDescription = "send"
                                )
                            }
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color(0xFFF2F3F5),      // 박스 내부 연한 회색
                            unfocusedBorderColor = Color(0xFF008080),
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { onSubmit() })
                    )
                }
            }

        }

    }
}