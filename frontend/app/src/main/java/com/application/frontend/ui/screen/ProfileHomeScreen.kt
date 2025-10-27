package com.application.frontend.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.frontend.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.frontend.viewmodel.MiniPostUi
import com.application.frontend.viewmodel.ProfileHomeViewModel
import com.application.frontend.viewmodel.ProfileUi
import com.application.frontend.viewmodel.ReviewUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatDateTime(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}

@Composable
fun ProfileHomeScreen(
    viewModel: ProfileHomeViewModel = hiltViewModel(),
    onEditProfile: (ProfileUi) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    ProfileHomeContent(
        user = uiState.profile,
        posts = uiState.posts,
        saved = uiState.saved,
        reviews = uiState.reviews,
        onEditProfile = onEditProfile
    )
}

@Composable
private fun ProfileHomeContent(
    user: ProfileUi = ProfileUi(nickname = "", email = "", verified = false),
    posts: List<MiniPostUi> = emptyList(),
    saved: List<MiniPostUi> = emptyList(),
    reviews: List<ReviewUi> = emptyList(),
    onEditProfile: (ProfileUi) -> Unit = {}
) {
    val brandTeal = Color(0xFF0B8A80)
    var selectedTab by remember { mutableStateOf(0) } // 0: Post, 1: Saved, 2: Reviews

    Surface(Modifier.fillMaxSize(), color = Color.White) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 헤더
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Profile",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.testTag("profile_title")
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { /* TODO settings */ }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = "settings",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // 프로필
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (val res = user.avatarRes) {
                        null -> Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                        )
                        else -> Image(
                            painter = painterResource(res),
                            contentDescription = "avatar",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(user.nickname, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user.email, color = Color(0xFF666666))
                        if (user.verified) {
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                painter = painterResource(R.drawable.ic_verified),
                                contentDescription = "verified",
                                tint = Color(0xFF21C25E)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { onEditProfile(user) },
                        modifier = Modifier
                            .padding(horizontal = 60.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Edit Profile", color = Color(0xFF6D6D6D))
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            // 탭 (세그먼트 스타일)
            item {
                SegmentedTabBar(
                    selected = selectedTab,
                    onSelect = { selectedTab = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .testTag("profile_segmented_tabs")
                )
                Spacer(Modifier.height(12.dp))
            }


            // 리스트
            when (selectedTab) {
                0 -> items(posts) { p -> PostCardMini(p) }
                1 -> items(saved) { p -> PostCardMini(p) }
                2 -> items(reviews) { r -> ReviewCard(r) }
            }
        }
    }
}

@Composable
private fun SegmentedTabBar(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val brandTeal = Color(0xFF0B8A80)
    val border = Color(0xFFE6E9EF)     // 외곽 라인
    val unselectedText = Color(0xFF8B96A1)

    Box(
        modifier = modifier
            .height(38.dp)
            .border(BorderStroke(1.dp, border), shape)
            .clip(shape)
            .background(Color.White)    // 스크린 배경이 흰색일 경우 스크린샷처럼 보임
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Post", "Saved", "Reviews").forEachIndexed { index, label ->
                val isSelected = index == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) brandTeal else Color.Transparent)
                        .clickable { onSelect(index) }
                        .testTag("profile_tab_$label"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else unselectedText,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}


@Composable
private fun PostCardMini(item: MiniPostUi) {
    val cardShape = RoundedCornerShape(12.dp)
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE6E9EF))
    ) {
        Column(Modifier.padding(14.dp)) {

            // 작성자 정보 (프로필 사진 + 이름)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.authorAvatarRes != null) {
                    Image(
                        painter = painterResource(item.authorAvatarRes),
                        contentDescription = "author avatar",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "author avatar",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(item.authorName, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = formatDateTime(item.createdAtMillis),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // 게시물 이미지(있으면)
            if (item.thumbnailRes != null) {
                val imageShape = RoundedCornerShape(10.dp)

                Image(
                    painter = painterResource(item.thumbnailRes),
                    contentDescription = "post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(imageShape),  // ✅ 둥근 모서리
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(12.dp))
            }

            // 본문
            Text(
                text = item.content,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                color = Color(0xFF222222)
            )

            Spacer(Modifier.height(10.dp))

            // 좋아요 + 댓글
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "likes")
                Spacer(Modifier.width(4.dp))
                Text("${item.likes}")

                Spacer(Modifier.width(16.dp))         // ← 좋아요 묶음과 댓글 묶음 간격

                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "comments")
                Spacer(Modifier.width(4.dp))
                Text("${item.comments}")
            }
        }
    }
}

@Composable
private fun ReviewCard(item: ReviewUi) {
    val cardShape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)        // 카드 바깥 여백
            .border(1.dp, Color(0xFFE6E9EF), cardShape)          // 테두리
            .clip(cardShape)
            .background(Color.White)                             // 박스 배경
            .padding(14.dp)                                      // 카드 내부 패딩
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 상단: 아이콘 + 날짜/시간
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chat_bubble),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = formatDateTime(item.createdAtMillis),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8B96A1)
                )
            }

            Spacer(Modifier.height(8.dp))

            // 본문 내용
            Text(
                text = item.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


/* ---------------- Preview 전용 샘플 (런타임 X) ---------------- */

@Preview(showBackground = true)
@Composable
private fun Preview_ProfileHomeScreen() {
    val now = System.currentTimeMillis()
    val user = ProfileUi(
        nickname = "Green Coach",
        email = "greencoach123@gmail.com",
        verified = true,
        avatarRes = R.drawable.ic_avatar_default
    )
    val posts = listOf(
        MiniPostUi(
            id = "1",
            authorName = "Green Coach",
            authorAvatarRes = R.drawable.ic_avatar_default,
            content = "이건 사진이 포함된 게시물입니다.",
            likes = 10,
            comments = 3,
            createdAtMillis = now - 3600_000,
            thumbnailRes = R.drawable.ic_post_test
        ),
        MiniPostUi(
            id = "2",
            authorName = "Green Coach",
            authorAvatarRes = R.drawable.ic_avatar_default,
            content = "이건 텍스트만 있는 게시물입니다. 내용이 길면 ... 처리됩니다.",
            likes = 5,
            comments = 2,
            createdAtMillis = now - 7200_000,
            thumbnailRes = null
        )
    )
    val reviews = listOf(
        ReviewUi("좋은 글이네요!", now - 3_600_000),   // 1시간 전
        ReviewUi("잘 봤습니다.",   now - 7_200_000)    // 2시간 전
    )

    ProfileHomeContent(
        user = user,
        posts = posts,
        saved = posts,
        reviews = reviews
    )
}