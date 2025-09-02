package com.application.frontend.ui.screen.community.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommentComposer(
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            // 로그인 전 임시 사용자명 노출
            label = { Text("Green Coach") },
            placeholder = { Text("메시지를 입력하세요") },
            singleLine = true,
            shape = RoundedCornerShape(24.dp)
        )
        IconButton(onClick = { /* TODO: 이모지 선택 시트 */ }) {
            Icon(Icons.Outlined.Mood, contentDescription = "emoji")
        }
        IconButton(onClick = onSubmit) {
            Icon(Icons.Outlined.Send, contentDescription = "send")
        }
    }
}