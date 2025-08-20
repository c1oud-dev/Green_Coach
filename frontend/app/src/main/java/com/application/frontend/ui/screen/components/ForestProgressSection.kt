package com.application.frontend.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ForestProgressSection(
    title: String,
    percent: Int,
    subtitle: String,
    onHelpClick: () -> Unit
) {
    val track = Color.White              // 배경선 색상 → 흰색
    val bar   = Color(0xFF008080)        // 진행색 → #008080
    val trackShape = RoundedCornerShape(percent = 50)

    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Progress", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text("${percent}% complete", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(5.dp))
        // 캡슐형 Progress bar (사각형 배경 제거)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)                          // 두께(원하면 10~14.dp 사이 조절)
                .background(track, shape = trackShape)  // 트랙 자체를 둥글게 칠함
                .clip(trackShape)                       // 자식도 같은 캡슐로 클립
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percent / 100f)       // 0f..1f (반드시 100f 로 나눔)
                    .background(bar, shape = trackShape)
            )
        }

        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            // 동그란 테두리 안의 ? 아이콘 느낌
            IconButton(
                onClick = onHelpClick,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)

            ) {
                Icon(Icons.Default.HelpOutline, contentDescription = "숲 성장 단계 도움말", tint = Color.Black)
            }
        }
    }
}