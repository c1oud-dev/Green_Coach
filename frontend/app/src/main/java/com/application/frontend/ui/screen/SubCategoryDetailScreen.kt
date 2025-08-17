package com.application.frontend.ui.screen

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.application.frontend.BuildConfig
import com.application.frontend.model.StepSection
import com.application.frontend.model.SubCategoryDetail
import com.application.frontend.ui.state.UiState
import com.application.frontend.viewmodel.SubCategoryDetailViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubCategoryDetailScreen(
    key: String,
    name: String,
    onBack: () -> Unit,
    vm: SubCategoryDetailViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key) { vm.load(key) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
        when (val state = uiState) {

            // NEW: 로딩 상태
            is UiState.Loading -> Box(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            // NEW: 성공 상태 → 기존 콘텐츠 그대로 사용
            is UiState.Success -> {
                SubCategoryDetailContent(
                    detail = state.data,
                    onBack = onBack
                )
            }

            // NEW: 에러 상태
            is UiState.Error -> Box(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(12.dp))
                    Row {
                        OutlinedButton(onClick = onBack) { Text("뒤로가기") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { vm.retry() }) { Text("재시도") }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubCategoryDetailContent(
    detail: SubCategoryDetail,
    onBack: () -> Unit
) {
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── 헤더 영역 ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color(AndroidColor.parseColor(detail.headerColor))),
                contentAlignment = Alignment.Center
            ) {
                // 뒤로가기 버튼
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .align(Alignment.TopStart)
                        .padding(start = 8.dp, top = 8.dp)
                ) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "뒤로",
                        tint = Color.White
                    )
                }

                val imageModel =
                    if (detail.imageUrl.startsWith("http", true)) detail.imageUrl
                    else BuildConfig.BASE_URL.trimEnd('/') + detail.imageUrl

                AsyncImage(
                    model = imageModel,
                    contentDescription = detail.name,
                    modifier = Modifier
                        .height(140.dp)
                        .padding(horizontal = 16.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // ── 본문 카드 ───────────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-24).dp), // 헤더에 겹치기
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 27.dp)) {
                    Text(
                        text = detail.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = detail.subtitle,
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )

                    Spacer(Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("♻️", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "버리는 방법",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Divider(Modifier.padding(vertical = 8.dp))

                    // 단계 & 불릿
                    detail.steps.forEachIndexed { idx, section ->
                        Text("${idx + 1}. ${section.title}", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        section.bullets.forEach { line ->
                            Row(Modifier.padding(start = 8.dp, bottom = 6.dp)) {
                                Text("• ")
                                Spacer(Modifier.width(4.dp))
                                Text(line)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    // 잘못된 예시
                    if (detail.wrongExamples.isNotEmpty()) {
                        Spacer(Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🚫", fontSize = 18.sp, color = Color(0xFFCC3B3B))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "잘못된 배출 예시",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Divider(Modifier.padding(vertical = 8.dp))
                        detail.wrongExamples.forEach {
                            Row(Modifier.padding(start = 8.dp, top = 6.dp)) {
                                Text("• ")
                                Spacer(Modifier.width(4.dp))
                                Text(it)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}
