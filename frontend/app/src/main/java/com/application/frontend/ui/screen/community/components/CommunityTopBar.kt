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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.application.frontend.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityTopBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    isLoggedIn: Boolean,
    unreadCount: Int
) {
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
                // ▸ 검색 박스 (둥근 모서리 + 연한 테두리 + 리소스 아이콘)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, Color(0xFFE5E8EB), RoundedCornerShape(22.dp))
                        .background(Color(0xFFF7F9FA))
                        .clickable { onSearchClick() }
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Search",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

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