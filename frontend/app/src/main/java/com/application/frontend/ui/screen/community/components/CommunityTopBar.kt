package com.application.frontend.ui.screen.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.application.frontend.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityTopBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    isLoggedIn: Boolean,
    unreadCount: Int,
    onQueryChange: (String) -> Unit
) {
    // ▸ 실제 검색 입력창 (입력 시 onQueryChange로 전달)
    var queryText by remember { mutableStateOf("") }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            scrolledContainerColor = Color.White
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ▸ 검색 박스
                OutlinedTextField(
                    value = queryText,
                    onValueChange = { new ->
                        queryText = new
                        onQueryChange(new)     // ▼ 입력될 때마다 ViewModel.setSearchQuery 호출
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 40.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp), // ← 폰트 약간 축소
                    placeholder = {
                        Text(
                            "Search",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)        // ← 아이콘도 살짝 축소
                        )
                    },
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF008080),
                        focusedBorderColor = Color(0xFF008080),
                        unfocusedContainerColor = Color(0xFFF7F9FA),
                        focusedContainerColor = Color.White
                    ),
                )

                Spacer(Modifier.width(10.dp))

                // ▸ 알림 버튼 (원형 컨테이너 + 테두리 + @drawable/ic_bell + 빨간 뱃지)
                Box(
                    modifier = Modifier.size(40.dp)
                ) {
                    // 원형 컨테이너
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFE5E8EB), CircleShape)
                            .background(Color.White)
                            .clickable { onNotificationClick() }
                    )
                    // 벨 아이콘 (리소스 사용)
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bell),
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(22.dp) // ⬅ 실제 아이콘 크기 지정
                    )
                    // 빨간 배지: "로그인 + 미확인 알림 존재" 일 때만 표시
                    val showBadge = isLoggedIn && unreadCount > 0
                    if (showBadge) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .size(16.dp) // ⬅ 배지도 살짝 축소 (18 -> 16)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935))
                        ) {
                            Text(
                                text = unreadCount.toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    )
}