package com.application.frontend.ui.screen.community.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.application.frontend.model.Comment
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clipToBounds
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

// 삭제 버튼 리빌 폭(버튼 56dp + 오른쪽 패딩 16dp)
private val DeleteBtnWidth = 56.dp
private val DeleteBtnHeight = 44.dp
private val DeleteBtnEndPadding = 16.dp
private val MaxRevealWidth = DeleteBtnWidth + DeleteBtnEndPadding

// ISO-8601 "2025-08-30T13:37:53.429227Z" → "just now" / "3 minutes ago" / "4 hours ago" / "2 days ago" / "yyyy.MM.dd"
private fun formatCommentTime(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    fun plural(n: Long, s: String, p: String) = if (n == 1L) s else p

    return try {
        val instant = Instant.parse(raw)            // 서버는 UTC ISO 문자열 유지
        val now = Instant.now()
        val diff = Duration.between(instant, now)
        val sec = kotlin.math.abs(diff.seconds)

        when {
            sec < 60 -> "just now"
            sec < 60 * 60 -> {
                val m = sec / 60
                "$m ${plural(m, "minute", "minutes")} ago"
            }
            sec < 24 * 60 * 60 -> {
                val h = sec / 3600
                "$h ${plural(h, "hour", "hours")} ago"
            }
            sec < 7 * 24 * 60 * 60 -> {
                val d = sec / 86400
                "$d ${plural(d, "day", "days")} ago"
            }
            else -> {
                DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.getDefault())
                    .format(instant.atZone(ZoneId.systemDefault()))
            }
        }
    } catch (_: DateTimeParseException) {
        raw // 혹시 ISO가 아니면 원문 노출
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentItem(
    comment: Comment,
    isReplyTarget: Boolean,
    replyTargetId: String?,
    onRowReply: (id: String, username: String) -> Unit,
    onDelete: (id: String) -> Unit,
    onCancelReply: () -> Unit,
) {
    val highlight = if (isReplyTarget) Color(0xFF007AFF).copy(alpha = 0.15f) else Color.Transparent
    val scope = rememberCoroutineScope()

    // 핵심 콘텐츠(스와이프 박스 안/밖에서 재사용)
    val core: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(highlight)
        ) {
            // 1) 콘텐츠
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRowReply(comment.id, comment.author) }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.Top
            ) {
                // 아바타
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                ) {
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
                        Spacer(Modifier.fillMaxSize())
                    }
                }
                Spacer(Modifier.width(12.dp))

                // 오른쪽: 헤더(@닉네임 | time) + 본문
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "@${comment.author}",
                            color = Color(0xFF9E9E9E),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = formatCommentTime(comment.timeText),
                            color = Color(0xFF9E9E9E),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = comment.content,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // ★ 답글모드일 때 오른쪽-위 'X' 떠 있게
            if (isReplyTarget) {
                Box(modifier = Modifier.matchParentSize()) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF9E9E9E),
                        contentColor = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 16.dp, top = 8.dp)
                            .size(26.dp)
                            .zIndex(1f)
                    ) {
                        IconButton(onClick = onCancelReply, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.Outlined.Close, contentDescription = "cancel reply")
                        }
                    }
                }
            }
        }
        // ▼ 대댓글 리스트(부모 아래에 렌더링)
        if (comment.replies.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                // 한 줄 UI를 만드는 람다: (Comment) -> Unit
                val replyRow: @Composable (Comment) -> Unit =
                    { rep ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 44.dp) // 아바타(40) + 간격(4)만큼 인덴트
                        ) {
                            // (간단화) 라인은 일단 제거해 높이 충돌 원인 제거
                            Spacer(Modifier.width(10.dp))

                            // 대댓글 콘텐츠
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // 작은 아바타
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                                Spacer(Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "@${rep.author}",
                                            color = Color(0xFF9E9E9E),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Text(
                                            text = formatCommentTime(rep.timeText),
                                            color = Color(0xFF9E9E9E),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = rep.content,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                // 각 대댓글에 스와이프 삭제 적용(내가 쓴 댓글만)
                comment.replies.forEach { rep ->
                    val isChildTarget = (replyTargetId == rep.id)

                    val contentWithOverlay: @Composable () -> Unit = {
                        Box(Modifier.fillMaxWidth()) {
                            // 대댓글 한 줄
                            replyRow(rep)

                            // 오버레이 (레이아웃 높이 영향 없음)
                            if (isChildTarget) {
                                Box(modifier = Modifier.matchParentSize()) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF9E9E9E),
                                        contentColor = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(end = 16.dp, top = 4.dp)
                                            .size(22.dp)
                                    ) {
                                        IconButton(onClick = onCancelReply, modifier = Modifier.fillMaxSize()) {
                                            Icon(Icons.Outlined.Close, contentDescription = "cancel reply")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (rep.isOwner) {
                        SwipeRevealRow(
                            revealWidth = MaxRevealWidth,
                            onDelete = { onDelete(rep.id) }
                        ) { _, _ ->
                            contentWithOverlay()
                        }
                    } else {
                        contentWithOverlay()
                    }

                }
            }
        }
    }   // ← end of core()

    // 부모 댓글: 내 댓글이면 '고정폭 리빌'로 변경
    if (comment.isOwner) {
        SwipeRevealRow(
            revealWidth = MaxRevealWidth,
            onDelete = {
                onDelete(comment.id)
            }
        ) { isOpen, close ->
            ParentRow(
                comment = comment,
                isReplyTarget = isReplyTarget,
                onRowReply = {
                    // 열려 있으면 먼저 닫기, 아니면 원래 동작(답글 진입)
                    if (isOpen) close() else onRowReply(comment.id, comment.author)
                },
                onCancelReply = onCancelReply
            )
        }

        // 스와이프 컨테이너 바깥에 대댓글 + Divider 고정
        RepliesBlock(
            parent = comment,
            replyTargetId = replyTargetId,
            onDelete = onDelete,
            onCancelReply = onCancelReply
        )
        HorizontalDivider(color = Color(0xFFE5E8EC))
    } else {
        core()
    }

}

@Composable
private fun RepliesBlock(
    parent: Comment,
    replyTargetId: String?,
    onDelete: (String) -> Unit,
    onCancelReply: () -> Unit
) {
    if (parent.replies.isEmpty()) return

    // 한 줄(대댓글) 본문 UI
    @Composable
    fun ReplyRow(rep: Comment) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 44.dp) // 부모에서 16dp 줬다고 가정 → 들여쓰기만
        ) {
            // (가이드 라인 대신) 간단한 여백만
            Spacer(Modifier.width(10.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // 아바타
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(Modifier.width(8.dp))

                Column(Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "@${rep.author}",
                            color = Color(0xFF9E9E9E),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = formatCommentTime(rep.timeText),
                            color = Color(0xFF9E9E9E),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = rep.content,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp) // bottom 여백은 누적 방지 위해 제거
    ) {
        parent.replies.forEach { rep ->
            // 항목 상태 꼬임 방지: 각 reply에 안정적인 key
            androidx.compose.runtime.key(rep.id) {
                val isChildTarget = (replyTargetId == rep.id)

                val contentWithOverlay: @Composable () -> Unit = {
                    // 🔑 BoxScope 안에서만 matchParentSize()가 가능
                    Box(Modifier.fillMaxWidth()) {
                        ReplyRow(rep)

                        if (isChildTarget) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()    // ⬅️ 이제 정상
                                    .zIndex(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF9E9E9E),
                                    contentColor = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(end = 16.dp, top = 4.dp)
                                        .size(22.dp)
                                ) {
                                    IconButton(
                                        onClick = onCancelReply,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            Icons.Outlined.Close,
                                            contentDescription = "cancel reply"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (rep.isOwner) {
                    SwipeRevealRow(
                        revealWidth = MaxRevealWidth,
                        onDelete = { onDelete(rep.id) }
                    ) { _, _ ->
                        contentWithOverlay() // 대댓글은 특별한 클릭 인터셉트 없음
                    }
                } else {
                    contentWithOverlay()
                }

            }
        }
    }
}



@Composable
private fun ParentRow(
    comment: Comment,
    isReplyTarget: Boolean,
    onRowReply: () -> Unit = {},
    onCancelReply: () -> Unit = {}
) {
    val highlight = if (isReplyTarget) Color(0xFF007AFF).copy(alpha = 0.15f) else Color.Transparent
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(highlight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRowReply() }
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.Top
        ) {
            // 아바타
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
            ) {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
                    Spacer(Modifier.fillMaxSize())
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("@${comment.author}", color = Color(0xFF9E9E9E),
                        style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.weight(1f))
                    Text(formatCommentTime(comment.timeText), color = Color(0xFF9E9E9E),
                        style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(6.dp))
                Text(comment.content, style = MaterialTheme.typography.bodyMedium,
                    maxLines = 8, overflow = TextOverflow.Ellipsis)
            }
        }

        if (isReplyTarget) {
            Box(Modifier.matchParentSize()) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF9E9E9E),
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp, top = 8.dp)
                        .size(26.dp)
                        .zIndex(1f)
                ) {
                    IconButton(onClick = onCancelReply, modifier = Modifier.fillMaxSize()) {
                        Icon(Icons.Outlined.Close, contentDescription = "cancel reply")
                    }
                }
            }
        }
    }
}

@Composable
private fun SwipeRevealRow(
    modifier: Modifier = Modifier,
    revealWidth: Dp = MaxRevealWidth,
    onDelete: () -> Unit,
    content: @Composable (isOpen: Boolean, close: () -> Unit) -> Unit
) {
    val density = LocalDensity.current
    val maxRevealPx = with(density) { revealWidth.toPx() }

    var rawOffsetX by remember { androidx.compose.runtime.mutableStateOf(0f) }  // 0f ~ -maxRevealPx
    val animatedOffsetX by animateFloatAsState(targetValue = rawOffsetX, label = "revealAnim")

    fun close() { rawOffsetX = 0f }
    val isOpen = animatedOffsetX < 0f

    // 부모가 자식 범위를 넘는 그리기를 잘라내도록 + 레이어 순서를 명확히
    Box(modifier.clipToBounds()) {

        // ── 뒤쪽 레이어(항상 깔아둠) ─────────────────────────────────────────────
        if (isOpen || animatedOffsetX != 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .zIndex(0f)
                    .clipToBounds(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = DeleteBtnEndPadding)
                        .size(width = DeleteBtnWidth, height = DeleteBtnHeight)
                        .background(Color(0xFFFF4D5E), RoundedCornerShape(12.dp)), // ← 확실한 붉은 배경
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "delete",
                        tint = Color.White
                    )
                    // 클릭 영역(버튼) 오버레이
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                onDelete()
                                close()
                            }
                    )
                }
            }
        }


        // ── 앞쪽 레이어(실제 행, 왼쪽으로만 이동) ───────────────────────────────
        Box(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth()
                // 닫혀 있을 때만 배경을 깔아 뒤 레이어 비침 방지
                .then(
                    if (!isOpen && animatedOffsetX == 0f)
                        Modifier.background(MaterialTheme.colorScheme.surface)
                    else Modifier
                )
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(maxRevealPx) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            val next = (rawOffsetX + dragAmount).coerceIn(-maxRevealPx, 0f)
                            rawOffsetX = next
                        },
                        onDragEnd = {
                            rawOffsetX = if (abs(rawOffsetX) >= maxRevealPx * 0.5f) -maxRevealPx else 0f
                        },
                        onDragCancel = { rawOffsetX = 0f }
                    )
                }
        ) {
            content(isOpen) { close() }
        }

    }
}

