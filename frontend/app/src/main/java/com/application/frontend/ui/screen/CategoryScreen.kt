package com.application.frontend.ui.screen

import android.content.res.Resources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.application.frontend.viewmodel.CategoryViewModel
import com.application.frontend.R
import com.application.frontend.model.Category
import com.application.frontend.navigation.Routes

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavHostController,
    categoryName: String,
    vm: CategoryViewModel = hiltViewModel()
) {
    val subCategories by vm.subs.collectAsState()
    val topCategories by vm.top.collectAsState()

    LaunchedEffect(Unit) { vm.loadTop() }      // 초기 로딩
    LaunchedEffect(categoryName) { vm.loadSubs(categoryName) }  // 기존 유지

    // ✅ 선택 상태를 로컬 상태로 관리 (초기값 = 전달받은 categoryName)
    var selectedCategory by remember(categoryName) { mutableStateOf(categoryName) }

    // ✅ 화면 진입/파라미터 변경 시 초기 서브카테고리 로딩
    LaunchedEffect(categoryName) {
        vm.loadSubs(categoryName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // ✅ 화면 전체 배경 흰색
    ) {
        // 1) AppBar (제목 가운데 정렬)
        CenterAlignedTopAppBar(
            title = {
                Text(
                    "카테고리",
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "뒤로가기",
                        modifier = Modifier.size(16.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White
            )
        )

        Row(modifier = Modifier.weight(1f)) {
            // 2) 왼쪽 사이드바
            LazyColumn(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .background(Color.White)
            ) {
                items(topCategories) { cat ->
                    val selected = cat.name == selectedCategory
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Row 바깥: 색 계산 (여기서는 Composable 사용 가능)
                        val rightLineColorSelected = Color(0xFF008080)
                        // 비선택일 때는 선을 그리지 않으므로 별도 색 불필요
                        val showRightLine = selected  // ← 선택 상태만 선 표시
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategory = cat.name
                                    vm.loadSubs(cat.name)
                                }
                                .background(if (selected) Color(0xFFF7F7FB) else Color.White)
                                .padding(vertical = 12.dp) // end 패딩 넣지 않기(끝에 딱 붙게)
                                .drawBehind {
                                    if (showRightLine) {
                                        val stroke = 3.dp.toPx()
                                        val lineHeight = 48.dp.toPx()     // 길이 원하는 대로 조절
                                        val x = size.width - stroke / 3   // 오른쪽 끝
                                        val y1 = size.height / 2f - lineHeight / 2f
                                        val y2 = size.height / 2f + lineHeight / 2f
                                        drawLine(
                                            color = rightLineColorSelected,
                                            start = Offset(x, y1),
                                            end = Offset(x, y2),
                                            strokeWidth = stroke
                                        )
                                    }
                                }
                        ) {
                            // 좌측 활성 인디케이터
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight()
                                    .background(if (selected) Color(0xFF008080) else Color.Transparent)
                            )
                            Spacer(Modifier.width(8.dp))

                            Icon(
                                painter = painterResource(id = cat.iconRes),
                                contentDescription = cat.name,
                                modifier = Modifier.size(24.dp),
                                tint = if (selected) Color(0xFF008080)
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(5.dp))

                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selected) Color(0xFF008080)
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                        }

                    }
                }
            }

            // ⬇️ 왼쪽 사이드(좌)와 오른쪽 콘텐츠(우) 사이 세로 구분선
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )


            // 4) 서브카테고리 그리드(오른쪽: 단일 카드 + 화살표로만 상세 이동)
            val (cardTitle, cardSubtitle, representativeKey) = remember(selectedCategory) {
                // 상위 카테고리별 대표 상세 키/타이틀/부제 매핑
                when (selectedCategory) {
                    "투명 페트병" -> Triple(
                        "투명 페트병 분리 배출",
                        "투명(무색) 생수·음료 페트병만별도 수거함에 배출",
                        "pet" // 현재 백엔드가 지원하는 대표 키
                    )
                    "플라스틱" -> Triple(
                        "플라스틱 분리 배출",
                        "투명 페트병 제외, 유색/불투명 페트병은 플라스틱류로 배출",
                        "plastic"
                    )
                    "비닐류" -> Triple(
                        "비닐류 분리 배출",
                        "물기 제거한 비닐만 배출",
                        "bag"
                    )
                    "스티로폼" -> Triple(
                        "스티로폼 분리 배출",
                        "포장재 EPS는 분리수거 대상이나, 컵라면 용기·코팅 ·오염품은 제외",
                        "styro"
                    )
                    "캔류" -> Triple(
                        "캔류 분리 배출",
                        "음료·통조림 캔은 캔류로 관리. 에어로졸·부탄가스 등 가스용기는 잔가스 완전 제거 후 배출",
                        "can" // 현재 백엔드가 지원하는 대표 키
                    )
                    "고철류" -> Triple(
                        "고철류 분리 배출",
                        "캔류와 구분되는 별도 품목(냄비·프라이팬 등 잡철). 캔류와 따로 분리가 원칙",
                        "steel" // 현재 백엔드가 지원하는 대표 키
                    )
                    "유리병" -> Triple(
                        "유리병 분리 배출",
                        "도자기·크리스탈 그릇은 유리병류 해당 아님",
                        "glass" // 현재 백엔드가 지원하는 대표 키
                    )
                    "종이류" -> Triple(
                        "종이류 분리 배출",
                        "오염·코팅·영수증(감열지) 등은 종이류 제외· 스프링·테이프 등 이물질 제거 원칙",
                        "paper" // 현재 백엔드가 지원하는 대표 키
                    )
                    "섬유류" -> Triple(
                        "섬유류 분리 배출",
                        "입기 어려운 의류 중심, 특정 품목(담요·가죽·방수 코팅 등) 제외",
                        "cloth" // 현재 백엔드가 지원하는 대표 키
                    )
                    "대형 전자제품" -> Triple(
                        "대형 전자제품 분리 배출",
                        "냉장고·세탁기 등은 환경부 ‘폐가전 무상방문수거’ 제도로 회수·재활용. 판매업자 무상 회수 의무 등 제도 병행",
                        "large" // 현재 백엔드가 지원하는 대표 키
                    )
                    "소형 전자제품" -> Triple(
                        "소형 전자제품 분리 배출",
                        "휴대폰·소형가전은 전용 수거함/지자체 거점 회수 또는 판매업자 회수",
                        "small" // 현재 백엔드가 지원하는 대표 키
                    )
                    "가구" -> Triple(
                        "가구 분리 배출",
                        "대형 가구는 지자체 대형폐기물 신고·수거 체계를 이용",
                        "furniture" // 현재 백엔드가 지원하는 대표 키
                    )
                    "전지류" -> Triple(
                        "전지류 분리 배출",
                        "전지류 전용 수거함 대상 위치·안내는 분리배출 누리집에서 제공",
                        "battery" // 현재 백엔드가 지원하는 대표 키
                    )
                    "음식물" -> Triple(
                        "음식물 분리 배출",
                        "음식물류 폐기물은 다른 생활폐기물과 섞지 말고, 물기 최대 제거 후 전용 수거 체계로 배출하는 것이 원칙",
                        "food" // 현재 백엔드가 지원하는 대표 키
                    )
                    else -> Triple(
                        "$selectedCategory 분리 배출",
                        "$selectedCategory 간략한 설명",
                        "pet_water" // 임시 기본값 (추후 카테고리별 키로 교체)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
                    .padding(6.dp)
            ) {
                Surface(
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(12.dp)
                        .border(1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(12.dp)) // ⬅️ 테두리 추가
                ) {
                    Column(Modifier.padding(16.dp)) {
                        // 카드 헤더: (1행) 타이틀 + 화살표  / (2행) 부제
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = cardTitle,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.weight(1f) // ⬅️ 남는 공간 차지해서 화살표를 오른쪽 끝으로
                                )

                                // 화살표(타이틀과 같은 라인)
                                IconButton(
                                    onClick = {
                                        navController.navigate(Routes.detail(representativeKey, cardTitle))
                                    },
                                    modifier = Modifier.size(20.dp) // 권장 터치 타겟
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_arrow_right),
                                        contentDescription = "상세보기"
                                    )
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = cardSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }


                        Spacer(Modifier.height(16.dp))

                        // 2×2 아이콘 그리드 (보기 전용)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 160.dp), // 아래 여백 확보
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            content = {
                                items(subCategories.take(6)) { sub ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        val context = LocalContext.current
                                        val safeIconRes = remember(sub.iconRes) {
                                            try {
                                                context.resources.getResourceTypeName(sub.iconRes)
                                                sub.iconRes
                                            } catch (_: Resources.NotFoundException) {
                                                R.drawable.ic_placeholder
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color.White,   // 내부 배경을 흰색으로
                                            modifier = Modifier
                                                .size(72.dp)
                                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                        ) {
                                            Image(
                                                painter = painterResource(id = sub.iconRes),
                                                contentDescription = sub.name,
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(12.dp))
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = sub.name,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
                // 카드 하단 여백
                Spacer(Modifier.height(24.dp))

            }
        }
    }
}