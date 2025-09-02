package com.application.frontend.ui.screen.community.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.application.frontend.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostComposer(
    text: String,
    repliesDisabled: Boolean,
    onTextChange: (String) -> Unit,
    onToggleReplies: () -> Unit,
    onPickImages: (List<Uri>) -> Unit,
    onPickGif: (String) -> Unit,
    onInsertEmoji: (String) -> Unit,
    onPostClick: () -> Unit,
    isLoggedIn: Boolean,
    onRequireLogin: () -> Unit
) {
    // use .value instead of delegated "by" to avoid getValue/setValue import issues
    val showGifSheet = remember { mutableStateOf(false) }
    val showEmojiMenu = remember { mutableStateOf(false) }

    // Android Photo Picker (이미지/동영상 여러 장)
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10),
        onResult = { uris -> if (uris.isNotEmpty()) onPickImages(uris) }
    )

    val contentStartPadding = 50.dp           // 아바타(38) + 간격(12) 기준
    val teal = Color(0xFF008080)
    val lineColor = Color(0xFFE8EEF1)

    Column(Modifier.fillMaxWidth()) {
        // 아바타 + 텍스트 입력
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Column(Modifier.weight(1f)) {
                Box {
                    TextField(
                        value = text,
                        onValueChange = onTextChange,
                        placeholder = { Text("내용을 입력하세요.") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = false
                    )
                    if (!isLoggedIn) {
                        // ★ 비로그인: 입력영역 클릭 시 로그인 모달 요청
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { onRequireLogin() }
                        )
                    }
                }
            }
        }

        // Everyone can reply / Replies disabled 토글
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = contentStartPadding)
                .clickable { onToggleReplies()}
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_globe_24),
                contentDescription = null,
                tint = teal
            )
            Spacer(Modifier.width(6.dp))
            Text(
                if (repliesDisabled) "Replies disabled" else "Everyone can reply",
                style = MaterialTheme.typography.labelLarge,
                color = teal
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = lineColor,
            modifier = Modifier.padding(start = contentStartPadding, top = 12.dp)
        )

        // 아이콘 행 + Post 버튼
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = contentStartPadding, top = 8.dp)
        ) {
            // 이미지/비디오
            IconButton(onClick = {
                if (!isLoggedIn) { onRequireLogin(); return@IconButton }
                imagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
            }) {
                Icon(
                    painterResource(R.drawable.ic_image_24),
                    contentDescription = "Pick media",
                    tint = teal,
                    modifier = Modifier.size(22.dp)
                )
            }

            // GIF 기본 제공 바텀시트
            IconButton(onClick = {
                if (!isLoggedIn) { onRequireLogin(); return@IconButton }
                showGifSheet.value = true
            }) {
                Icon(
                    painterResource(R.drawable.ic_gif_24),
                    contentDescription = "Pick GIF",
                    tint = teal,
                    modifier = Modifier.size(22.dp)
                )
            }

            // 이모지 기본 메뉴
            Box {
                IconButton(onClick = {
                    if (!isLoggedIn) { onRequireLogin(); return@IconButton }
                    showEmojiMenu.value = true
                }) {
                    Icon(
                        painterResource(R.drawable.ic_emoji_24),
                        contentDescription = "Emoji",
                        tint = teal,
                        modifier = Modifier.size(22.dp)
                    )
                }
                DropdownMenu(
                    expanded = showEmojiMenu.value,
                    onDismissRequest = { showEmojiMenu.value = false }
                ) {
                    val emojis = listOf("😀","😍","🔥","👏","🎉","✨","👍","💡","🧠","📚")
                    emojis.forEach { e ->
                        DropdownMenuItem(
                            text = { Text(e, style = MaterialTheme.typography.titleLarge) },
                            onClick = {
                                onInsertEmoji(e)
                                showEmojiMenu.value = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // 아이콘과 버튼 사이 얇은 세로 줄
            VerticalDivider(
                thickness = 1.dp,
                color = lineColor,
                modifier = Modifier.height(28.dp)
            )
            Spacer(Modifier.width(12.dp))

            Button(
                onClick = {
                    if (!isLoggedIn) onRequireLogin() else onPostClick()
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = teal)
            ) { Text("Post") }
        }
    }

    // 간단한 GIF 선택 바텀시트 (샘플)
    if (showGifSheet.value) {
        ModalBottomSheet(onDismissRequest = { showGifSheet.value = false }) {
            Column(Modifier.padding(16.dp)) {
                Text("Choose a GIF", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                val gifs = listOf(
                    "https://media.giphy.com/media/ICOgUNjpvO0PC/giphy.gif",
                    "https://media.giphy.com/media/13HgwGsXF0aiGY/giphy.gif",
                    "https://media.giphy.com/media/3oKIPtjElfqwMOTbH2/giphy.gif",
                    "https://media.giphy.com/media/l0IylOPCNkiqOgMyA/giphy.gif",
                    "https://media.giphy.com/media/5GoVLqeAOo6PK/giphy.gif",
                    "https://media.giphy.com/media/111ebonMs90YLu/giphy.gif"
                )
                gifs.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { url ->
                            AssistChip(
                                onClick = {
                                    onPickGif(url)
                                    showGifSheet.value = false
                                },
                                label = { Text("GIF") }
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}