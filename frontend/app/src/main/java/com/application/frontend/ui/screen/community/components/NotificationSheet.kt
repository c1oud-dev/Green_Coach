package com.application.frontend.ui.screen.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.application.frontend.model.Notification
import com.application.frontend.model.NotificationType
import java.time.Duration
import java.time.Instant
import kotlin.math.roundToInt

/* 로컬 enum 금지 → 파일 상단으로 이동 */
private enum class SwipePos { LEFT_REVEAL, CENTER, RIGHT_FULL }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSheet(
    notifications: List<Notification>,
    onDismiss: () -> Unit,
    onMarkAllRead: () -> Unit,
    onRead: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notifications", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onMarkAllRead) {
                    Text("Mark all as read")
                    Icon(Icons.Outlined.DoneAll, contentDescription = null)
                }
            }

            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE8EEF1))
            Spacer(Modifier.height(4.dp))

            LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                items(items = notifications, key = { it.id }) { n ->
                    NotificationSwipeItem(
                        item = n,
                        onRead = { onRead(n.id) },       // 오른쪽 끝까지 스와이프 시 읽음
                        onDelete = { onDelete(n.id) }    // Delete 버튼 탭 시 삭제
                    )
                }
            }
        }
    }
}

/** "3분 전/2시간 전/어제" 식 상대 시간 */
private fun relativeFrom(createdAt: Instant?): String {
    if (createdAt == null) return ""
    val d = Duration.between(createdAt, Instant.now())
    val mins = d.toMinutes()
    val hours = d.toHours()
    val days = d.toDays()
    return when {
        mins < 1 -> "방금 전"
        mins < 60 -> "${mins}분 전"
        hours < 24 -> "${hours}시간 전"
        days == 1L -> "어제"
        days < 7 -> "${days}일 전"
        else -> "${days / 7}주 전"
    }
}

@Composable
private fun NotificationRow(n: Notification) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {

        // 아바타 + 읽지 않음 점(좌상단)
        Box(modifier = Modifier.size(36.dp)) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(Color(0xFFE9ECEF)),
                contentAlignment = Alignment.Center
            ) {
                Text((n.actorName ?: "U").first().uppercase(), style = MaterialTheme.typography.labelLarge)
            }
            if (!n.read) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-3).dp, y = (-3).dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E88E5))
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            val actor = n.actorName ?: "Someone"
            val target = n.replyToName     // ← Notification 모델에 추가(② 참고)

            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(actor) }
                    when (n.type) {
                        NotificationType.REPLY -> {
                            append(" replied to ")
                            if (!target.isNullOrBlank()) {
                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(target) }
                            } else {
                                append("your comment")
                            }
                        }
                        else -> { // COMMENT, SYSTEM 등
                            append(" commented")
                        }
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                relativeFrom(n.createdAt),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/* ──────────────────────────────────────────────────────────────────────── */
/* 커스텀 스와이프 아이템: 왼쪽 = Delete 버튼만 노출(버튼 탭 시 삭제),         */
/*                           오른쪽 = 끝까지 스와이프 시 읽음 처리             */
/* ──────────────────────────────────────────────────────────────────────── */
@Composable
private fun NotificationSwipeItem(
    item: Notification,
    onRead: () -> Unit,
    onDelete: () -> Unit
) {
    val deleteWidth = 112.dp
    val rightFull = 320.dp // 끝까지 밀기 기준(고정 폭, 실제 카드폭보다 커도 무방)

    // px 변환
    val deletePx = with(LocalDensity.current) { deleteWidth.toPx() }
    val rightPx = with(LocalDensity.current) { rightFull.toPx() }

    var offsetPx by remember { mutableStateOf(0f) }

    fun animateTo(target: SwipePos) {
        offsetPx = when (target) {
            SwipePos.LEFT_REVEAL -> -deletePx
            SwipePos.CENTER      -> 0f
            SwipePos.RIGHT_FULL  -> rightPx
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
    ) {
        // 왼쪽 Read 배경
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFF5B6FFF)),    // ← 직사각형
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(start = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Email, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Read", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }

        // 오른쪽 Delete 버튼
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(deleteWidth)
                .fillMaxHeight()
                .background(Color(0xFFEA4335))     // ← 직사각형
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color.White)
                Spacer(Modifier.height(4.dp))
                Text("Delete", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }


        // 포그라운드 카드(수평 드래그)
        Surface(
            modifier = Modifier
                .offset { IntOffset(offsetPx.roundToInt(), 0) }
                .fillMaxWidth()
                .height(86.dp)
                .zIndex(2f) // ← 카드가 항상 배경 위에 오게
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            // 스냅 규칙
                            when {
                                offsetPx <= -deletePx / 2 -> animateTo(SwipePos.LEFT_REVEAL) // 왼쪽: 버튼만
                                offsetPx >= rightPx * 0.95f -> {                               // 오른쪽: 사실상 끝
                                    onRead()
                                    animateTo(SwipePos.CENTER)
                                }
                                else -> animateTo(SwipePos.CENTER)
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetPx = (offsetPx + dragAmount)
                            .coerceIn(-deletePx, rightPx) // 왼쪽은 버튼 폭까지만
                    }
                },
            shape = RectangleShape,              // ⬅ 직사각형 테두리
            color = Color.White
        ) {
            NotificationRow(item)
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 6.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}