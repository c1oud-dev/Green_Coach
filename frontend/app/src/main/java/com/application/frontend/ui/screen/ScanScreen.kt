package com.application.frontend.ui.screen

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .background(greenTeal)
        ) {
            // 상단 타이틀
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Scan Items",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // 하단 흰색 영역
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.White,
                        RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                    )
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // 스캔 카드
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { onScanClick() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = greenTeal)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 스캔 아이콘 (카메라 아이콘)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera_scan), // 스캔 아이콘 리소스 필요
                            contentDescription = "Scan",
                            modifier = Modifier.size(64.dp),
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
                                .width(220.dp)
                                .height(50.dp)
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

                Spacer(modifier = Modifier.height(40.dp))

                // Recent scan 섹션
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
                    Text(
                        text = "View all",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable { onRecentScanClick() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recent scan 리스트 (처음 2개만)
                val recentScans = uiState.scanHistory.take(2)
                recentScans.forEach { scan ->
                    ScanHistoryItem(scan = scan)
                    Spacer(modifier = Modifier.height(12.dp))
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
            Text(
                text = "${scan.leafPoints} leafs",
                fontSize = 16.sp, // 14sp → 16sp
                fontWeight = FontWeight.SemiBold, // Medium → SemiBold
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFF4CAF50).copy(alpha = 0.15f), // 0.2f → 0.15f (더 연하게)
                        RoundedCornerShape(8.dp) // 12dp → 8dp
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp) // 8dp,4dp → 6dp,2dp
            ) {
                Text(
                    text = "confirmed",
                    fontSize = 10.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecentScansModal(
    scans: List<ScanHistoryDto>,
    onClose: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = Color.White,
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent scan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 전체 리스트
            LazyColumn {
                items(scans) { scan ->
                    ScanHistoryItem(scan = scan)
                    Spacer(modifier = Modifier.height(12.dp))
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
        ScanHistoryDto(3, "Cashback from purchase", "16 Sep 2023 16:08 PM", 175),
        ScanHistoryDto(4, "Transfer to card", "16 Sep 2023 11:21 AM", 9000),
        ScanHistoryDto(5, "Transfer to card", "15 Sep 2023 11:21 AM", 9267)
    )

    ScanMainScreen(
        uiState = ScanUiState(scanHistory = mockHistory),
        onScanClick = {},
        onRecentScanClick = {},
        showRecentScans = false,
        onCloseRecentScans = {}
    )
}