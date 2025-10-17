package com.application.frontend.ui.screen

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import com.application.frontend.R
import com.application.frontend.viewmodel.ScanViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
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
    onDone: () -> Unit
) {
    val greenTeal = Color(0xFF66CBD2)
    val darkTeal = Color(0xFF008080)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(greenTeal)
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Button(
                onClick = onDone,
                colors = ButtonDefaults.buttonColors(
                    containerColor = darkTeal
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Done",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        // 이미지 영역 (실제 프로젝트에서는 촬영한 이미지 표시)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            // 여기에 실제 스캔한 이미지나 AI 분석 시각화가 들어갈 예정
            Icon(
                painter = painterResource(id = R.drawable.ic_pet), // 임시 아이콘
                contentDescription = "Scanned Item",
                modifier = Modifier.size(120.dp),
                tint = Color.Unspecified
            )
        }

        // 하단 결과 카드
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // 결과 제목
                Text(
                    text = result.category,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // 재활용 가능 여부
                Text(
                    text = "Recyclable",
                    fontSize = 16.sp,
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 설명 텍스트
                Text(
                    text = result.description,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // Read More 버튼
                Button(
                    onClick = { /* 상세 정보 페이지로 이동 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkTeal
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Read More",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = "Arrow",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
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