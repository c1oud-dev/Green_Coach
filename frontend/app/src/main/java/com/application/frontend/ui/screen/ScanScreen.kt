package com.application.frontend.ui.screen

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.frontend.R
import com.application.frontend.navigation.Routes
import com.application.frontend.viewmodel.ScanViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    navController: NavController,
    viewModel: ScanViewModel = hiltViewModel()
) {
    // --- state / helpers ---
    val uiState by viewModel.uiState.collectAsState()
    var showRecentScans by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }

    // 카메라 촬영 런처(기본 카메라 앱)
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingImageUri != null) {
            val uri = pendingImageUri!!
            val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            if (bytes == null) {
                viewModel.onError("이미지를 읽을 수 없습니다.")
            } else {
                val req = bytes.toRequestBody(mime.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData(
                    name = "image",
                    filename = "scan.jpg",
                    body = req
                )
                // 서버 분석 호출
                viewModel.analyzeImage(part)
                showResult = true
            }
        } else {
            // 촬영 취소/실패는 조용히 무시하거나 필요 시 안내
        }
    }

    // 권한 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // 권한 허용 시 촬영 시작
            fun startTakePicture() {
                val contentValues = ContentValues().apply {
                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        "greencoach_scan_${System.currentTimeMillis()}.jpg"
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                if (uri == null) {
                    viewModel.onError("사진 저장 위치를 만들 수 없습니다.")
                    return
                }
                pendingImageUri = uri
                takePictureLauncher.launch(uri)
            }
            startTakePicture()
        } else {
            viewModel.onError("카메라 권한이 거부되었습니다.")
        }
    }

    // --- 화면 분기(when) ---
    when {
        showResult && uiState.latestResult != null -> {
            ScanResultScreen(
                result = uiState.latestResult!!,
                onBack = { showResult = false },
                onDone = {
                    showResult = false
                    viewModel.clearResult()
                },
                onReadMore = { key, name ->
                    navController.navigate(Routes.detail(key, name))
                }
            )
        }

        else -> {
            ScanMainScreen(
                uiState = uiState,
                onScanClick = {
                    // 기존: viewModel.startScan() → 결과 화면
                    // 변경: CAMERA 권한 요청 → 기본 카메라 앱 실행 → 촬영/업로드
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onRecentScanClick = { showRecentScans = true },
                showRecentScans = showRecentScans,
                onCloseRecentScans = { showRecentScans = false }
            )
        }
    }

    // --- 로딩 오버레이 ---
    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    // --- 에러 스낵바 ---
    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }
    SnackbarHost(hostState = snackbarHostState)
}

@Composable
private fun ScanMainScreen(
    uiState: ScanUiState,
    onScanClick: () -> Unit,
    onRecentScanClick: () -> Unit,
    showRecentScans: Boolean,
    onCloseRecentScans: () -> Unit
) {
    val greenTeal = Color(0xFF66CBD2)
    val darkTeal = Color(0xFF008080)

    Box(modifier = Modifier.fillMaxSize()) {
        // 메인 컨텐츠
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // ✅ 화면 배경 흰색
        ) {
            // 상단 타이틀
            Surface(
                color = greenTeal,
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 25.dp,
                    bottomEnd = 24.dp
                ),
                shadowElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()          // ⬅️ 좌우 여백 없이 전체 폭
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .statusBarsPadding(), // ⬅️ 내용만 상태바 아래로 내림 (배경은 그대로)
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Scan Items",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // 본문 영역
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // 스캔 카드
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)                 // 가로폭
                        .align(Alignment.CenterHorizontally) // 가운데 정렬
                        .height(240.dp)
                        .clickable { onScanClick() },
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = greenTeal)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 스캔 아이콘 (카메라 아이콘)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera_scan),
                            contentDescription = "Scan",
                            modifier = Modifier.size(70.dp),
                            tint = Color.Black.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 스캔 버튼
                        Button(
                            onClick = onScanClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = darkTeal
                            ),
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Scan your scrap item",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Recent scan 섹션
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), // 화면 좌우와 여백
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE6E6E6)), // ✅ 얇은 회색 테두리
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        //헤더
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent scan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            OutlinedButton(
                                onClick = onRecentScanClick,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(
                                    text = "View all",
                                    fontSize = 12.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color(0x1A000000), thickness = 1.dp) // 헤더 하단 얇은 선

                        // Recent scan 리스트 (처음 2개만)
                        val recentScans = uiState.scanHistory.take(2)
                        recentScans.forEach { scan ->
                            ScanHistoryItem(scan = scan)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        // Recent scans 전체 목록 모달
        if (showRecentScans) {
            RecentScansModal(
                scans = uiState.scanHistory,
                onClose = onCloseRecentScans
            )
        }
    }
}

@Composable
private fun ScanHistoryItem(scan: ScanHistoryDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘
        Box(
            modifier = Modifier
                .size(44.dp) // 48dp → 44dp
                .background(
                    Color(0xFFF5F5F5), // 더 연한 회색
                    RoundedCornerShape(12.dp) // CircleShape → RoundedCornerShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = when (scan.category.lowercase()) {
                        "plastic" -> R.drawable.ic_pet
                        "can" -> R.drawable.ic_can
                        else -> R.drawable.ic_placeholder
                    }
                ),
                contentDescription = scan.category,
                modifier = Modifier.size(20.dp),
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 텍스트 정보
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = scan.category,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold, // Medium → SemiBold
                color = Color.Black
            )
            Text(
                text = scan.scannedAt,
                fontSize = 12.sp,
                color = Color(0xFF999999) // Color.Gray → 더 구체적인 색상
            )
        }

        // 우측 정보
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFF4CAF50).copy(alpha = 0.12f), // 연한 그린 배경
                        RoundedCornerShape(999.dp)             // pill 형태
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${scan.leafPoints} leafs",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32)                 // 진한 그린 텍스트
                )
            }
        }
    }
}

@Composable
private fun RecentScansModal(
    scans: List<ScanHistoryDto>,
    onClose: () -> Unit
) {
    val config = LocalConfiguration.current
    val screenWidth  = config.screenWidthDp.dp
    val screenHeight = config.screenHeightDp.dp
    val targetWidth = screenWidth * 0.92f     // 📏 폭 ≈ 92%
    val targetMaxHeight = screenHeight * 0.72f // 📏 최대 높이 ≈ 72%

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 3.dp,
                shadowElevation = 6.dp,
                color = Color.White,
                modifier = Modifier
                    .width(targetWidth)
                    .heightIn(max = targetMaxHeight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // 헤더
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent scan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        OutlinedButton(
                            onClick = onClose,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Close", fontSize = 12.sp, color = Color(0xFF757575))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0x1A000000), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(scans) { scan ->
                            ScanHistoryItem(scan = scan)
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFFEAEAEA), thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanResultScreen(
    result: ScanResultDto,
    onBack: () -> Unit,
    onDone: () -> Unit,
    onReadMore: (String, String) -> Unit
) {
    val pageBackground = Color(0xFFFFFFFF)
    val accentColor = Color(0xFF5EC9D0)
    val accentDark = Color(0xFF008080)
    val confidencePercent = (result.confidence * 100).toInt().coerceIn(0, 100)
    val resolvedContent = remember(result) { resolveScanResultContent(result) }
    val classificationMessage = "AI가 분석한 결과 ${resolvedContent.categoryTitle}으로 분류되었어요. 정확도 ${confidencePercent}%"
    val recyclableLabel = resolvedContent.label
    val detailName = resolvedContent.detailName
    val detailKey = resolvedContent.detailKey

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Text(
                text = "Result",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Button(
                onClick = onDone,
                colors = ButtonDefaults.buttonColors(containerColor = accentDark),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Done",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // 하단 결과 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.18f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 결과 제목
                Text(
                    text = "정확도",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentDark
                )

                // 재활용 가능 여부
                Spacer(modifier = Modifier.height(20.dp))

                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(150.dp)) {
                        val strokeWidth = size.minDimension * 0.12f
                        drawArc(
                            color = accentColor.copy(alpha = 0.25f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = accentColor,
                            startAngle = -90f,
                            sweepAngle = 360f * (confidencePercent / 100f),
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${confidencePercent}%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 설명 텍스트
                Text(
                    text = classificationMessage,
                    fontSize = 13.sp,
                    color = Color(0xFF2C3E50),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = resolvedContent.categoryTitle,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = recyclableLabel,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2E7D32),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = resolvedContent.description,
            fontSize = 14.sp,
            color = Color(0xFF3C3C3C),
            lineHeight = 21.sp
        )

        if (resolvedContent.tips.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                resolvedContent.tips.forEach { tip ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "•",
                            fontSize = 16.sp,
                            color = accentDark,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = tip,
                            fontSize = 14.sp,
                            color = Color(0xFF3C3C3C),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onReadMore(detailKey, detailName) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentDark),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Read More",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "Go to detail",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private data class ScanResultContent(
    val categoryTitle: String,
    val label: String,
    val description: String,
    val tips: List<String>,
    val detailKey: String,
    val detailName: String
)

private enum class RecognizedScanCategory {
    PET,
    PLASTIC,
    BAG,
    STYRO,
    CAN,
    STEEL,
    GLASS,
    PAPER,
    CLOTH,
    LARGE,
    SMALL,
    BATTERY,
    FURNITURE,
    FOOD
}

private fun resolveScanResultContent(result: ScanResultDto): ScanResultContent {
    val recognizedCategory = detectScanCategory(result)
    return when (recognizedCategory) {
        RecognizedScanCategory.PET -> ScanResultContent(
            categoryTitle = "투명 페트병",
            label = "뚜껑·라벨 제거 후 전용 배출함 이용",
            description = "투명(무색) 생수·음료 페트병은 내용물을 완전히 비우고 가볍게 헹군 뒤 라벨과 뚜껑을 제거해야 고품질 재활용이 가능해요. 병 몸통은 찌그러뜨려 부피를 줄인 다음 투명 페트병 전용 수거함에 넣어주세요.",
            tips = listOf(
                "온수로 라벨 접착제를 부드럽게 녹이면 쉽게 떼어낼 수 있어요.",
                "뚜껑은 플라스틱 뚜껑류로 따로 배출하세요.",
                "부피를 줄여 모으면 수거함 공간을 절약할 수 있어요."
            ),
            detailKey = mapDetailKey("투명 페트병"),
            detailName = "투명 페트병"
        )

        RecognizedScanCategory.PLASTIC -> ScanResultContent(
            categoryTitle = "플라스틱",
            label = "깨끗한 플라스틱 용기만 분리배출",
            description = "샴푸 용기, 식품 용기 등 일반 플라스틱은 내용물을 비우고 물기와 이물질을 제거한 뒤 마른 상태로 배출해야 재활용 공정에 투입될 수 있어요. 유색·불투명 페트병도 플라스틱류로 함께 배출합니다.",
            tips = listOf(
                "라벨·펌프 등 이물질을 제거하세요.",
                "기름기가 심하면 세제를 풀어 세척하세요.",
                "재활용이 어려운 일회용품은 생활폐기물과 구분하세요."
            ),
            detailKey = mapDetailKey("플라스틱"),
            detailName = "플라스틱"
        )

        RecognizedScanCategory.BAG -> ScanResultContent(
            categoryTitle = "비닐류",
            label = "물기 제거한 비닐만 배출",
            description = "과자 봉지나 비닐 포장재는 내용물과 이물질을 털어내고 물기가 남지 않도록 말린 뒤 투명 비닐봉투에 모아 배출하세요. 음식물 묻은 비닐은 일반 종량제 봉투에 버리는 것이 원칙입니다.",
            tips = listOf(
                "테이프·라벨을 떼어낸 뒤 배출하세요.",
                "여러 장을 겹쳐 묶어 두면 수거가 쉬워요.",
                "PVC 등 재활용이 어려운 비닐은 일반 쓰레기로 분리하세요."
            ),
            detailKey = mapDetailKey("비닐류"),
            detailName = "비닐류"
        )

        RecognizedScanCategory.STYRO -> ScanResultContent(
            categoryTitle = "스티로폼",
            label = "깨끗한 포장재만 재활용",
            description = "아이스박스나 완충재 등 스티로폼은 음식물이나 테이프를 제거하고 물기를 없앤 뒤 부피를 줄여 배출하세요. 컵라면 용기처럼 코팅되었거나 음식물이 스며든 제품은 재활용이 어렵습니다.",
            tips = listOf(
                "테이프와 스티커는 완전히 떼어내세요.",
                "부피가 큰 경우 잘라서 배출하면 좋아요.",
                "오염된 스티로폼은 종량제 봉투로 버리세요."
            ),
            detailKey = mapDetailKey("스티로폼"),
            detailName = "스티로폼"
        )

        RecognizedScanCategory.CAN -> ScanResultContent(
            categoryTitle = "캔류",
            label = "내용물 비우고 압착 배출",
            description = "음료·통조림 캔은 내용물을 비우고 물기를 제거한 뒤 가능하면 눌러 부피를 줄여주세요. 에어로졸이나 부탄가스 등 가스용기는 잔가스를 완전히 배출한 후 구멍을 뚫어 안전하게 배출해야 합니다.",
            tips = listOf(
                "철과 알루미늄을 섞지 말고 재질별로 따로 배출하세요.",
                "라벨과 플라스틱 뚜껑은 분리하세요.",
                "녹이 심한 캔은 일반 금속류와 함께 배출하세요."
            ),
            detailKey = mapDetailKey("캔류"),
            detailName = "캔류"
        )

        RecognizedScanCategory.STEEL -> ScanResultContent(
            categoryTitle = "고철류",
            label = "철제 생활용품은 고철류로",
            description = "냄비, 프라이팬, 철제 옷걸이 등 금속제품은 음식물과 비닐을 제거하고 고철류 전용 수거함에 배출하세요. 플라스틱 손잡이나 유리 뚜껑 등 이물질은 분리해 주세요.",
            tips = listOf(
                "부속품은 분해하여 재질별로 분리하세요.",
                "날카로운 부분은 종이로 감싸 안전을 확보하세요.",
                "대형 금속류는 지자체 수거서비스를 이용하세요."
            ),
            detailKey = mapDetailKey("고철류"),
            detailName = "고철류"
        )

        RecognizedScanCategory.GLASS -> ScanResultContent(
            categoryTitle = "유리병",
            label = "뚜껑 분리 후 색상별 분리",
            description = "술병이나 음료수 병은 내용물을 비우고 헹군 뒤 금속이나 플라스틱 뚜껑을 분리하세요. 일부 지자체는 투명·갈색·녹색 등 색상별로 나눠 배출하면 재활용 효율이 좋아집니다.",
            tips = listOf(
                "깨진 유리는 신문지에 싸서 일반쓰레기로 버리세요.",
                "라벨이 젖으면 쉽게 떼어낼 수 있어요.",
                "유리병은 포개지 말고 세워서 배출하세요."
            ),
            detailKey = mapDetailKey("유리병"),
            detailName = "유리병"
        )

        RecognizedScanCategory.PAPER -> ScanResultContent(
            categoryTitle = "종이류",
            label = "테이프 제거 후 마른 종이만",
            description = "박스나 신문 등 종이류는 스티커, 테이프, 철심을 제거한 뒤 납작하게 접어 묶어 배출하세요. 코팅지나 기름·음식물이 묻은 종이는 재활용이 어렵습니다.",
            tips = listOf(
                "종이팩은 전용 수거함이나 세척 후 별도 배출하세요.",
                "영수증(감열지)은 일반쓰레기로 분리하세요.",
                "박스는 납작하게 접어 부피를 줄이세요."
            ),
            detailKey = mapDetailKey("종이류"),
            detailName = "종이류"
        )

        RecognizedScanCategory.CLOTH -> ScanResultContent(
            categoryTitle = "섬유류",
            label = "입지 않는 옷은 건조 후 배출",
            description = "의류나 신발 등 섬유류는 세탁 또는 건조해 곰팡이나 냄새가 나지 않게 한 뒤 헌옷 수거함에 넣어주세요. 젖어 있거나 오염된 제품은 일반 폐기물로 처리됩니다.",
            tips = listOf(
                "버클·단추 등 금속부품은 제거하세요.",
                "커다란 담요는 묶어서 흘러내리지 않도록 하세요.",
                "가죽·방수 코팅 제품은 일반폐기물로 분류하세요."
            ),
            detailKey = mapDetailKey("섬유류"),
            detailName = "섬유류"
        )

        RecognizedScanCategory.LARGE -> ScanResultContent(
            categoryTitle = "대형 전자제품",
            label = "폐가전 무상 방문수거 대상",
            description = "냉장고, 세탁기 등 대형 가전은 지자체 또는 환경부의 폐가전 무상 방문수거를 신청해 배출해야 합니다. 사전 예약 후 문앞에 비치하면 전문 수거원이 안전하게 회수합니다.",
            tips = listOf(
                "제품 내부 내용물을 비우고 전원을 차단하세요.",
                "문이나 호스 등 부속품을 함께 준비하세요.",
                "수거 예약일 전날 재확인 연락을 받으면 응답해 주세요."
            ),
            detailKey = mapDetailKey("대형 전자제품"),
            detailName = "대형 전자제품"
        )

        RecognizedScanCategory.SMALL -> ScanResultContent(
            categoryTitle = "소형 전자제품",
            label = "전용 수거함 또는 판매점 회수",
            description = "휴대폰, 드라이기 등 소형 전자제품은 전용 수거함이나 동 주민센터, 대형마트의 회수함에 배출하세요. 배터리가 포함된 제품은 분리하거나 테이프로 단자를 감아 안전을 확보해야 합니다.",
            tips = listOf(
                "개인정보가 있는 기기는 초기화 후 배출하세요.",
                "분리 가능한 배터리는 따로 모아 전지류로 배출하세요.",
                "케이블은 묶어서 정리하면 수거가 편합니다."
            ),
            detailKey = mapDetailKey("소형 전자제품"),
            detailName = "소형 전자제품"
        )

        RecognizedScanCategory.BATTERY -> ScanResultContent(
            categoryTitle = "전지류",
            label = "단자 절연 후 전용함에",
            description = "건전지, 보조배터리 등은 단자를 절연테이프로 감싸고 전지류 전용 수거함에 넣어주세요. 부풀거나 손상된 배터리는 지자체 환경센터에 문의해 안전하게 처리해야 합니다.",
            tips = listOf(
                "여러 개를 묶어 배출하지 말고 개별 포장하세요.",
                "충전식 배터리는 완전히 방전한 뒤 배출하세요.",
                "리튬 배터리는 화재 위험이 있어 일반 쓰레기로 버리면 안 됩니다."
            ),
            detailKey = mapDetailKey("전지류"),
            detailName = "전지류"
        )

        RecognizedScanCategory.FURNITURE -> ScanResultContent(
            categoryTitle = "가구",
            label = "대형폐기물 신고 후 배출",
            description = "소파나 책상 등 가구는 지자체 대형폐기물 배출 신고 후 지정된 스티커를 부착해 약속된 장소에 배출해야 합니다. 부품을 분해해 부피를 줄이면 수거가 수월해요.",
            tips = listOf(
                "수거 예약 후 스티커를 잘 보이게 붙이세요.",
                "유리 등 다른 재질은 분리해 주세요.",
                "비나 눈을 맞지 않도록 배출 직전에 내놓으세요."
            ),
            detailKey = mapDetailKey("가구"),
            detailName = "가구"
        )

        RecognizedScanCategory.FOOD -> ScanResultContent(
            categoryTitle = "음식물",
            label = "물기 제거 후 전용 용기에",
            description = "음식물류 폐기물은 수분을 최대한 제거해 전용 수거 용기나 종량제 봉투에 담아 배출하세요. 동물 뼈나 티백 등 재활용이 어려운 잔재는 일반 쓰레기로 분류합니다.",
            tips = listOf(
                "물기를 빼기 위해 채반에 한번 거르세요.",
                "염분이 높은 음식은 일반 쓰레기로 버리세요.",
                "수거 일정에 맞춰 밀폐된 용기에 보관하세요."
            ),
            detailKey = mapDetailKey("음식물"),
            detailName = "음식물"
        )

        null -> {
            val fallbackName = (result.subCategory?.takeIf { it.isNotBlank() }
                ?: result.category.takeIf { it.isNotBlank() }
                ?: "미분류")
            val fallbackLabel = result.subCategory?.takeIf { it.isNotBlank() }
                ?: "재활용 안내"
            val fallbackDescription = result.description.takeIf { it.isNotBlank() }
                ?: "$fallbackName 분리배출 정보가 아직 등록되어 있지 않습니다. 최신 정보를 확인해 주세요."
            val fallbackTips = if (result.tips.isNotEmpty()) result.tips else emptyList()
            ScanResultContent(
                categoryTitle = fallbackName,
                label = fallbackLabel,
                description = fallbackDescription,
                tips = fallbackTips,
                detailKey = mapDetailKey(fallbackName),
                detailName = fallbackName
            )
        }
    }
}

private fun detectScanCategory(result: ScanResultDto): RecognizedScanCategory? {
    val combined = listOfNotNull(result.subCategory, result.category)
        .joinToString(separator = " ")
        .lowercase()
    return when {
        (combined.contains("투명") && combined.contains("플라스틱")) -> RecognizedScanCategory.PET
        combined.contains("페트") -> RecognizedScanCategory.PET
        combined.contains("pet") && combined.contains("bottle") -> RecognizedScanCategory.PET
        combined.contains("plastic bottle") -> RecognizedScanCategory.PET
        combined.contains("플라스틱") || combined.contains("plastic") -> RecognizedScanCategory.PLASTIC
        combined.contains("비닐") || combined.contains("vinyl") -> RecognizedScanCategory.BAG
        combined.contains("스티로") || combined.contains("styro") || combined.contains("eps") -> RecognizedScanCategory.STYRO
        combined.contains("캔") || combined.contains("can") || combined.contains("aluminum") -> RecognizedScanCategory.CAN
        combined.contains("고철") || combined.contains("철") || combined.contains("metal") || combined.contains("steel") -> RecognizedScanCategory.STEEL
        combined.contains("유리") || combined.contains("glass") -> RecognizedScanCategory.GLASS
        combined.contains("종이") || combined.contains("paper") || combined.contains("cardboard") -> RecognizedScanCategory.PAPER
        combined.contains("섬유") || combined.contains("의류") || combined.contains("cloth") || combined.contains("textile") -> RecognizedScanCategory.CLOTH
        combined.contains("대형") && (combined.contains("전자") || combined.contains("appliance") || combined.contains("electronics")) -> RecognizedScanCategory.LARGE
        combined.contains("소형") && (combined.contains("전자") || combined.contains("appliance") || combined.contains("electronics")) -> RecognizedScanCategory.SMALL
        combined.contains("전지") || combined.contains("battery") -> RecognizedScanCategory.BATTERY
        combined.contains("가구") || combined.contains("furniture") -> RecognizedScanCategory.FURNITURE
        combined.contains("음식") || combined.contains("food") -> RecognizedScanCategory.FOOD
        else -> null
    }
}

// 데이터 클래스들
data class ScanUiState(
    val isLoading: Boolean = false,
    val latestResult: ScanResultDto? = null,
    val scanHistory: List<ScanHistoryDto> = emptyList(),
    val error: String? = null
)

data class ScanResultDto(
    val category: String,
    val subCategory: String?,
    val confidence: Double,
    val description: String,
    val tips: List<String>
)

data class ScanHistoryDto(
    val id: Long,
    val category: String,
    val scannedAt: String,
    val leafPoints: Int,
    val confirmed: Boolean = true
)

@Preview(showBackground = true)
@Composable
private fun ScanScreenPreview() {
    // 미리보기용 가짜 데이터
    val mockHistory = listOf(
        ScanHistoryDto(1, "Plastic", "17 Sep 2023 11:21 AM", 10),
        ScanHistoryDto(2, "Can", "17 Sep 2023 10:34 AM", 3),
        ScanHistoryDto(3, "data1", "16 Sep 2023 16:08 PM", 1),
        ScanHistoryDto(4, "data2", "16 Sep 2023 11:21 AM", 9),
        ScanHistoryDto(5, "data3", "15 Sep 2023 11:21 AM", 7)
    )

    ScanMainScreen(
        uiState = ScanUiState(scanHistory = mockHistory),
        onScanClick = {},
        onRecentScanClick = {},
        showRecentScans = false,
        onCloseRecentScans = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun ScanResultScreenPreview() {
    val previewResult = ScanResultDto(
        category = "Plastic Bottle",
        subCategory = "투명 플라스틱",
        confidence = 0.6,
        description = "플라스틱 병을 버릴 때에는 먼저 라벨과 스티커를 깨끗이 떼어내고 뚜껑을 분리하여 버리는 것이 좋아요. 내용물이 남아 있지 않도록 충분히 헹궈서 배출하면 더 효과적으로 분리수거돼요.",
        tips = listOf(
            "라벨이 잘 떨어지지 않는다면 따뜻한 물에 담가두었다가 제거해 주세요.",
            "분리 배출 후에는 다른 재활용품과 섞이지 않도록 묶어 보관하면 좋아요."
        )
    )

    ScanResultScreen(
        result = previewResult,
        onBack = {},
        onDone = {},
        onReadMore = { _, _ -> }
    )
}

private fun mapDetailKey(detailName: String): String {
    val normalized = detailName.trim()
    val fallback = normalized.lowercase().replace(" ", "_")
    return when {
        normalized.contains("투명", ignoreCase = true) && normalized.contains("페트", ignoreCase = true) -> "pet"
        normalized.contains("플라스틱", ignoreCase = true) || normalized.contains("plastic", ignoreCase = true) -> "plastic"
        normalized.contains("비닐", ignoreCase = true) || normalized.contains("vinyl", ignoreCase = true) -> "bag"
        normalized.contains("스티로", ignoreCase = true) || normalized.contains("styro", ignoreCase = true) -> "styro"
        normalized.contains("캔", ignoreCase = true) || normalized.contains("can", ignoreCase = true) -> "can"
        normalized.contains("고철", ignoreCase = true) || normalized.contains("steel", ignoreCase = true) || normalized.contains("metal", ignoreCase = true) -> "steel"
        normalized.contains("유리", ignoreCase = true) || normalized.contains("glass", ignoreCase = true) -> "glass"
        normalized.contains("종이", ignoreCase = true) || normalized.contains("paper", ignoreCase = true) -> "paper"
        normalized.contains("섬유", ignoreCase = true) || normalized.contains("cloth", ignoreCase = true) -> "cloth"
        normalized.contains("대형", ignoreCase = true) && normalized.contains("전자", ignoreCase = true) -> "large"
        normalized.contains("소형", ignoreCase = true) && normalized.contains("전자", ignoreCase = true) -> "small"
        normalized.contains("전지", ignoreCase = true) || normalized.contains("battery", ignoreCase = true) -> "battery"
        normalized.contains("가구", ignoreCase = true) || normalized.contains("furniture", ignoreCase = true) -> "furniture"
        normalized.contains("음식", ignoreCase = true) || normalized.contains("food", ignoreCase = true) -> "food"
        fallback.isNotEmpty() -> fallback
        else -> "unknown"
    }
}