package com.application.frontend.ui.screen

import android.content.res.Resources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.application.frontend.viewmodel.CategoryViewModel
import com.application.frontend.R
import com.application.frontend.navigation.Routes

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavHostController,
    categoryName: String,
    vm: CategoryViewModel = hiltViewModel()
) {
    val subCategories by vm.subs.collectAsState()
    val topCategories = vm.topCategories

    // ✅ 선택 상태를 로컬 상태로 관리 (초기값 = 전달받은 categoryName)
    var selectedCategory by remember(categoryName) { mutableStateOf(categoryName) }

    // ✅ 화면 진입/파라미터 변경 시 초기 서브카테고리 로딩
    LaunchedEffect(categoryName) {
        vm.loadSubs(categoryName)
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
                   Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                           .fillMaxWidth()
                           // ① 클릭 영역 & 배경
                            .clickable {
                                selectedCategory = cat.name
                                vm.loadSubs(cat.name)
                            }
                            .background(if (selected) Color(0xFFF7F7FB) else Color.White)
                           .padding(vertical = 12.dp)
                    ) {
                      // ② Active Indicator
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
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = if (selected) Color(0xFF008080)
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            /*// 3) 구분선
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )*/

            // 4) 서브카테고리 그리드
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(subCategories) { sub ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            /* TODO: 클릭 처리 */
                            if (sub.key.isNotEmpty()) {
                                navController.navigate(Routes.detail(sub.key, sub.name))
                            }
                        }
                    ) {
                        // ① Context 얻기
                        val context = LocalContext.current

                        // ② 유효한 아이콘 리소스 ID 계산
                        val safeIconRes = remember(sub.iconRes) {
                            try {
                                // 존재하지 않으면 예외
                                context.resources.getResourceTypeName(sub.iconRes)
                                // 예외 없으면 원래 ID 사용
                                sub.iconRes
                            } catch (_: Resources.NotFoundException) {
                                // 예외 시 플레이스홀더로 대체
                                R.drawable.ic_placeholder
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            modifier = Modifier.size(72.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            // 원 내부에 사진을 "그대로" 담기: 잘리지 않도록 Fit 사용
                            Image(
                                painter = painterResource(id = safeIconRes),
                                contentDescription = sub.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    //.padding(12.dp)         // 가장자리 여백(원에 딱 맞추려면 줄이거나 제거)
                                    .clip(CircleShape),     // 표면과 동일하게 원형으로 클립
                                contentScale = ContentScale.Fit // 크기만 맞추고 크롭 없음
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
        }
    }
}
