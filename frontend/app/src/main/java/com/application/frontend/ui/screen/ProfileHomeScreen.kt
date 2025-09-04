package com.application.frontend.ui.screen

import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.frontend.R
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 런타임 더미 없음 —> 화면 외부에서 주입
data class ProfileUi(
    val nickname: String,
    val email: String,
    val verified: Boolean,
    val avatarRes: Int? = null // 기본 아바타 없으면 null
)

data class MiniPostUi(
    val id: String,
    val authorName: String,        // 작성자 이름
    val authorAvatarRes: Int? = null, // 작성자 프로필 이미지 (없으면 null)
    val content: String,          // 본문(길면 … 처리)
    val likes: Int,               // 좋아요 수
    val comments: Int,            // 댓글 수
    val createdAtMillis: Long,    // 작성 시각(UTC or local) - 날짜+시간 표기용
    val thumbnailRes: Int? = null // 이미지 없으면 null
)

private fun formatDateTime(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}

@Composable
fun ProfileHomeScreen(
    user: ProfileUi = ProfileUi(nickname = "", email = "", verified = false),
    posts: List<MiniPostUi> = emptyList(),
    saved: List<MiniPostUi> = emptyList(),
    reviews: List<String> = emptyList(),
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
                            .padding(horizontal = 70.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Edit Profile", color = Color(0xFF6D6D6D))
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            // 탭
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = brandTeal,
                    divider = {}
                ) {
                    Tab(text = { Text("Post") }, selected = selectedTab == 0, onClick = { selectedTab = 0 })
                    Tab(text = { Text("Saved") }, selected = selectedTab == 1, onClick = { selectedTab = 1 })
                    Tab(text = { Text("Reviews") }, selected = selectedTab == 2, onClick = { selectedTab = 2 })
                }
                Spacer(Modifier.height(12.dp))
            }

            // 리스트
            when (selectedTab) {
                0 -> items(posts) { p -> PostCardMini(p) }
                1 -> items(saved) { p -> PostCardMini(p) }
                2 -> items(reviews) { r -> ReviewRow(r) }
            }
        }
    }
}

@Composable
private fun PostCardMini(item: MiniPostUi) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
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
                Image(
                    painter = painterResource(item.thumbnailRes),
                    contentDescription = "post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
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
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "likes")
                Text("${item.likes}")
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "comments")
                Text("${item.comments}")
            }
        }
    }
}


@Composable
private fun ReviewRow(text: String) {
    ListItem(
        headlineContent = { Text(text) },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_chat_bubble),
                contentDescription = null
            )
        }
    )
    Divider()
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
    ProfileHomeScreen(
        user = user,
        posts = posts,
        saved = posts,
        reviews = listOf("좋은 글이네요!", "잘 봤습니다.")
    )
}