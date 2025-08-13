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
import com.application.frontend.viewmodel.SubCategoryDetailViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubCategoryDetailScreen(
    key: String,
    name: String,
    onBack: () -> Unit,
    vm: SubCategoryDetailViewModel = hiltViewModel()
) {
    val detail by vm.detail.collectAsState()

    LaunchedEffect(key) { vm.load(key) }

    Scaffold { inner ->
        when (val d = detail) {
            null -> Box(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            else -> {
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
                            .height(260.dp) // 상단 여유 확보
                            .background(Color(AndroidColor.parseColor(d.headerColor))),
                        contentAlignment = Alignment.Center
                    ) {
                        // 뒤로가기 버튼(상단 가장자리까지 색 채우고 상태바 패딩 적용)
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .statusBarsPadding()
                                .align(Alignment.TopStart)
                                .padding(start = 8.dp, top = 8.dp)
                        ) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "뒤로", tint = Color.White)
                        }

                        val imageModel =
                            if (d.imageUrl.startsWith("http", true)) d.imageUrl
                            else BuildConfig.BASE_URL.trimEnd('/') + d.imageUrl

                        AsyncImage(
                            model = imageModel,
                            contentDescription = d.name,
                            modifier = Modifier
                                .height(180.dp)
                                .padding(horizontal = 32.dp),
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
                                text = "${d.name}병",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = d.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(20.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("♻️", fontSize = 18.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "버리는 방법",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Divider(Modifier.padding(top = 8.dp, bottom = 12.dp))

                            d.steps.forEachIndexed { idx, section ->
                                Text(
                                    text = "${idx + 1}. ${section.title}",
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(6.dp))
                                section.bullets.forEach { line ->
                                    Row(Modifier.padding(start = 8.dp, bottom = 6.dp)) {
                                        Text("• ")
                                        Spacer(Modifier.width(2.dp))
                                        Text(line)
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                            }

                            if (d.wrongExamples.isNotEmpty()) {
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
                                Divider(Modifier.padding(top = 8.dp, bottom = 12.dp))

                                d.wrongExamples.forEach {
                                    Row(Modifier.padding(start = 8.dp, top = 8.dp)) {
                                        Text("• ")
                                        Spacer(Modifier.width(2.dp))
                                        Text(it)
                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * 미리보기
 */
@Preview(showBackground = true)
@Composable
fun SubCategoryDetailScreenPreview() {
    val fakeDetail = SubCategoryDetail(
        key = "bottle",
        name = "생수병",
        imageUrl = "/images/bottle.png",
        headerColor = "#66CBD2", // 실제 색상
        subtitle = "생수병은 재활용이 매우 중요한 자원이기 때문에, 깨끗하고 분리된 상태로 배출하는 것이 핵심이에요.",
        steps = listOf(
            StepSection("내용물 비우기", listOf(
                "병 안에 물이 남아 있지 않도록 완전히 비워주세요.",
                "물뿐 아니라 이물질(음료, 우유, 커피 등)이 섞인 경우 세척 후 재활용, 아니면 일반 쓰레기로 버려야 해요."
            )),
            StepSection("라벨 제거", listOf(
                "대부분 생수병 라벨은 점선 처리가 되어 있어 손쉽게 찢을 수 있습니다.",
                "라벨을 반드시 제거하고, 비닐류로 따로 분리배출해야 합니다. (같은 투명한 재질이라도 PET병과는 재질이 달라요.)"
            ))
        ),
        wrongExamples = listOf(
            "잘못된 상태처리 결과라벨 붙은 채 배출재활용 효율 급감, 분류 시 폐기될 수 있음",
            "내용물 남은 병오염물로 간주되어 일반 쓰레기 처리",
            "병 안에 빨대 등 이물질재활용 불가병을 통째로 묶어서 배출자동화 선별기에서 분류 실패 가능"
        )
    )

    SubCategoryDetailContent(detail = fakeDetail, onBack = {})
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
            // ── 헤더 ──────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp) // 살짝 키워서 여백 확보
                    .background(Color(AndroidColor.parseColor(detail.headerColor))),
                contentAlignment = Alignment.Center
            ) {
                // ← 상단 끝까지 색 채우고, 버튼만 상태바 아래로 내림
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .align(Alignment.TopStart)
                        .padding(start = 8.dp, top = 8.dp)
                ) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "뒤로", tint = Color.White)
                }

                val imageModel =
                    if (detail.imageUrl.startsWith("http", true)) detail.imageUrl
                    else BuildConfig.BASE_URL.trimEnd('/') + detail.imageUrl

                AsyncImage(
                    model = imageModel,
                    contentDescription = detail.name,
                    modifier = Modifier
                        .height(180.dp)
                        .padding(horizontal = 32.dp),
                    contentScale = ContentScale.Fit
                )
            }


            // ── 본문 ──────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-24).dp), // 헤더 위로 살짝 겹치기
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 27.dp)) {
                    Text(
                        text = detail.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = detail.subtitle,
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("♻️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "버리는 방법",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Divider(Modifier.padding(vertical = 8.dp))

                    detail.steps.forEachIndexed { idx, step ->
                        Text("${idx + 1}. ${step.title}", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        step.bullets.forEach { line ->
                            Row(Modifier.padding(start = 8.dp, bottom = 6.dp)) {
                                Text("• ")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(line)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    if (detail.wrongExamples.isNotEmpty()) {
                        Spacer(Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🚫", fontSize = 18.sp, color = Color(0xFFCC3B3B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "잘못된 배출 예시",
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

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
