package com.application.frontend.ui.screen.community.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReactionRow(
    likeCount: Int,
    commentCount: Int,
    onLike: () -> Unit,
    onBookmark: () -> Unit,
    onComment: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // 좋아요 아이콘 + 숫자
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLike) {
                    Icon(Icons.Filled.FavoriteBorder, contentDescription = "Like")
                }
                Text(formatCount(likeCount))
            }
            // 댓글 아이콘 + 숫자
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onComment) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline, // 말풍선 아이콘
                        contentDescription = "Comment"
                    )
                }
                Text(commentCount.toString())
            }
        }
        // 오른쪽: 공유 + 북마크
        Row {
            IconButton(onClick = { /* share */ }) {
                Icon(Icons.Filled.IosShare, contentDescription = "Share")
            }
            IconButton(onClick = onBookmark) {
                Icon(Icons.Filled.BookmarkBorder, contentDescription = "Bookmark")
            }
        }
    }
}

private fun formatCount(n: Int): String =
    if (n >= 1000) String.format("%.1fk", n / 1000f) else n.toString()