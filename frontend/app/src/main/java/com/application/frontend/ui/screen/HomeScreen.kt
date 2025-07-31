package com.application.frontend.ui.screen

import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.frontend.R
import com.application.frontend.model.Category
import com.application.frontend.viewmodel.NewsViewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.application.frontend.model.NewsDto

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(vm: NewsViewModel = viewModel()) {
    var searchText by remember { mutableStateOf("") }
    val newsList   = vm.news
    val hashtags   = listOf("#캔", "#발광경질", "#부직포가방", "#페트병", "#유리병")
    val categories = listOf(
        Category(R.drawable.ic_pet,     "페트병"),
        Category(R.drawable.ic_plastic, "플라스틱 용기"),
        Category(R.drawable.ic_bag,     "비닐류"),
        Category(R.drawable.ic_styro,   "스티로폼"),
        Category(R.drawable.ic_can,     "캔류"),
        Category(R.drawable.ic_glass,   "유리병"),
        Category(R.drawable.ic_paper,   "종이류"),
        Category(R.drawable.ic_milk,    "종이팩"),
        Category(R.drawable.ic_box,     "박스/골판지"),
        Category(R.drawable.ic_cloth,   "옷/섬유류"),
        Category(R.drawable.ic_bulb,    "소형가전"),
        Category(R.drawable.ic_washer,  "대형가전")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 56.dp)
    ) {
        // 헤더 + 검색창
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB4E0DA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_leaf_logo),
                                contentDescription = "앱 로고",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(Modifier.width(5.dp))
                            Text("Green", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF008080))
                            Text("Coach", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Icon(Icons.Filled.Notifications, contentDescription = "알림", modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.height(30.dp))
                    Text("분리배출, 오늘부터 함께 시작해요.", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Let’s contribution to our earth.", fontSize = 10.sp)
                    Spacer(Modifier.height(30.dp))

                }
            }
        }

        // HomeScreen.kt – 검색창 부분
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-24).dp)
            ) {
                // 흰 배경
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color.White, RoundedCornerShape(20.dp))
                )
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        vm.fetch(it)
                    },
                    // 텍스트 스타일로 폰트 크기 지정
                    textStyle = LocalTextStyle.current.copy(fontSize = 10.sp),
                    placeholder = {
                        Text("Search Recycling Trash")
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            tint = Color(0xFF008080)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),       // 높이를 48dp로 늘려 텍스트 영역 확보
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }


        // 해시태그
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hashtags) { tag ->
                    Text(
                        text = tag,
                        fontSize = 8.sp,
                        modifier = Modifier
                            .background(Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 카테고리
        item {
            Spacer(Modifier.height(25.dp))
            Text(
                "Category",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                categories.chunked(4).forEach { rowCats ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowCats.forEach { cat ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(cat.iconRes),
                                    contentDescription = cat.name,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Unspecified
                                )
                                Text(cat.name, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // 뉴스
        item {
            Spacer(Modifier.height(25.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("News", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Learn more", fontSize = 12.sp, color = Color.Gray)
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 중복 URL을 제거한 뒤 순회( 1) link 로 중복 제거, 2) NewsDto 타입 명시)
                items(newsList.distinctBy { it.link }) { item: NewsDto ->  // :contentReference[oaicite:3]{index=3}
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(200.dp)
                    ) {
                        Column {
                            // Coil AsyncImage로 실제 URL 로드, 로딩/에러 시 placeholder 표시
                            AsyncImage(
                                model = item.image,
                                contentDescription = item.title,
                                modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                                placeholder = painterResource(R.drawable.ic_news_placeholder),
                                error       = painterResource(R.drawable.ic_news_placeholder),
                                contentScale = ContentScale.Crop
                            )  // :contentReference[oaicite:4]{index=4}
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.timeAgo, fontSize = 10.sp, color = Color.Gray)
                                Text(item.press, fontSize = 10.sp, color = Color.Gray)
                            }
                            Text(
                                item.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}