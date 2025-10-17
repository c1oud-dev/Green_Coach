package com.application.frontend.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.frontend.R
import com.application.frontend.viewmodel.NewsViewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.application.frontend.model.NewsDto
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import com.application.frontend.navigation.Routes
import com.application.frontend.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    vm: NewsViewModel = hiltViewModel(),   // ✅ Hilt 주입
    catVm: CategoryViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf("") }
    var showNotFound by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // ▼ 서버에서 가져온 최상위 카테고리 사용
    val categories by catVm.top.collectAsState()
    val hashtags by catVm.hashtags.collectAsState()
    LaunchedEffect(Unit) { catVm.loadTop() }

    val newsList   = vm.news

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),   // ✅ 홈 화면 배경 흰색
        contentPadding = PaddingValues(bottom = 56.dp)
    ) {
        // 헤더 + 검색창
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xC76FC7C7))
                    .statusBarsPadding()
            ) {
                Column(modifier = Modifier.padding(23.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_leaf_logo),
                                contentDescription = "앱 로고",
                                modifier = Modifier.size(28.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(Modifier.width(5.dp))
                            Text("Green", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF008080))
                            Text("Coach", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    Spacer(Modifier.height(28.dp))
                    Text("분리배출, 오늘부터 함께 시작해요.", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Let’s contribution to our earth.", fontSize = 12.sp)
                    Spacer(Modifier.height(24.dp))

                }
            }
        }

        // 검색창 + 해시태그
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)   // 좌우 여백 동일
                    .offset(y = (-24).dp)          // 기존처럼 위로 살짝 겹치기
            ) {
                // ─ 검색창 ─
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },  // ← 뉴스와 완전 분리
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        color = Color(0xFF484C52)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            val q = searchText
                            coroutineScope.launch {
                                val result = catVm.search(q)
                                if (result != null) {
                                    navController.navigate(Routes.detail(result.key, result.name))
                                } else {
                                    showNotFound = true
                                }
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE0E3E7), RoundedCornerShape(28.dp))
                                .padding(horizontal = 18.dp)
                        ) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = null,
                                tint = Color(0xFF008080),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Box(Modifier.weight(1f)) {
                                if (searchText.isEmpty()) {
                                    Text("Search Recycling Trash", color = Color(0xFFB6BDC4), fontSize = 16.sp)
                                }
                                innerTextField()
                            }
                        }
                    }
                )

                // 검색창과 해시태그 사이 간격
                Spacer(Modifier.height(5.dp))

                // ─ 해시태그 ─
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 7.dp) // 검색창과 동일 폭 유지
                ) {
                    items(hashtags) { tag ->
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            color = Color(0xFF868889),
                            modifier = Modifier
                                .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(18.dp))
                                .background(Color.White, RoundedCornerShape(18.dp))
                                .padding(horizontal = 12.dp, vertical = 0.dp)
                        )
                    }
                }

                if (showNotFound) {
                    AlertDialog(
                        onDismissRequest = { showNotFound = false },
                        confirmButton = {
                            Button(
                                    onClick = { showNotFound = false },
                                    colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF008080) // 버튼 배경색
                                                )
                                        ) {
                                    Text("확인", color = Color.White)
                                }
                        },
                        title = { Text("검색 결과", fontWeight = FontWeight.SemiBold) },
                        text  = { Text("해당 항목이 존재하지 않습니다.") },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color.White
                    )
                }
            }
        }


        // ─ 카테고리 ─
        item {
            Text(
                "Category",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp)
            )
        }

        item {
            Spacer(Modifier.height(15.dp))

            // 4열 고정 그리드: 마지막 줄(전지류/음식물)도 왼쪽부터 일정 칸에 배치
            val columns = 4
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                categories.chunked(columns).forEach { rowCats ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 4칸을 항상 채워서 정렬이 어긋나지 않도록 함
                        for (i in 0 until columns) {
                            if (i < rowCats.size) {
                                val cat = rowCats[i]
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)           // 각 칸 동일 너비
                                        .clickable { navController.navigate("category/${cat.name}") }
                                ) {
                                    // ▶ 아이콘 보이는 크기 통일: 정사각 박스(56dp) + 중앙 정렬 + Fit
                                    Box(
                                        modifier = Modifier.size(56.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(cat.iconRes),
                                            contentDescription = cat.name,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(6.dp),   // 아이콘 여백(필요시 4~8dp로 미세조정)
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        cat.name,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            } else {
                                // 빈 칸: 마지막 줄 정렬이 어색해지지 않도록 자리만 차지
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }


        // 뉴스
        item {
            Spacer(Modifier.height(15.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("News", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Learn more", fontSize = 12.sp, color = Color.Gray)
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 중복 URL을 제거한 뒤 순회( 1) link 로 중복 제거, 2) NewsDto 타입 명시)
                items(newsList.distinctBy { it.link }) { item: NewsDto ->  // :contentReference[oaicite:3]{index=3}
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.width(200.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White) // ← 배경 흰색
                    ) {
                        Column {
                            // Coil AsyncImage로 실제 URL 로드, 로딩/에러 시 placeholder 표시
                            AsyncImage(
                                model = item.image,
                                contentDescription = item.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                placeholder = painterResource(R.drawable.ic_news_placeholder),
                                error       = painterResource(R.drawable.ic_news_placeholder),
                                contentScale = ContentScale.Crop
                            )  // :contentReference[oaicite:4]{index=4}
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 6.dp, end = 6.dp, top = 1.dp, bottom = 0.dp), // ↓ 하단 여백 2dp
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.timeAgo, fontSize = 10.sp, color = Color.Gray)
                                Text(item.press, fontSize = 10.sp, color = Color.Gray)
                            }
                            Text(
                                text = item.title,
                                style = LocalTextStyle.current.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 16.sp, // ← 줄 간격 타이트(필요하면 15~16.sp로 미세 조정)
                                    // 위아래 여백을 줄여 실제 보이는 줄 간격을 더 촘촘하게
                                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                                    lineHeightStyle = LineHeightStyle(
                                        alignment = LineHeightStyle.Alignment.Center,
                                        trim = LineHeightStyle.Trim.Both
                                    )
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 0.dp, bottom = 0.dp)
                            )

                        }
                    }
                }
            }

        }
    }
}