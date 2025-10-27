package com.application.frontend

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.application.frontend.navigation.Routes
import com.application.frontend.navigation.Screen
import com.application.frontend.ui.screen.*
import com.application.frontend.ui.theme.GCGray
import com.application.frontend.ui.theme.GCTeal
import com.application.frontend.viewmodel.CommunityViewModel
import com.application.frontend.viewmodel.ScanViewModel

// 아이콘 사이즈 상수
private val BottomBarIconSize = 28.dp   // 하단 탭 아이콘
private val FabIconSize = 30.dp         // 중앙 카메라(FAB) 아이콘
private val ScanSlotIconSize = BottomBarIconSize // 가운데 슬롯(투명 아이콘)도 동일


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    // ✅ Scan 결과를 Forest에 반영하기 위해 공유 ViewModel 구독
    val scanVm: ScanViewModel = hiltViewModel()
    val scanUi by scanVm.uiState.collectAsState()
    // shots 계산 방식: 확정된 스캔 수(confirmed) 또는 단순히 히스토리 개수
    val shots = scanUi.scanHistory.count { it.confirmed } // 또는: scanUi.scanHistory.size
    val totalLeafs = scanUi.scanHistory.filter { it.confirmed }.sumOf { it.leafPoints }

    val bottomTabs = listOf(
        Screen.Home,
        Screen.Forest,
        Screen.Scan,
        Screen.Community,
        Screen.Profile
    )

    val hideChromeRoutes = setOf(
        Routes.SignUp, Routes.ForgotPassword, Routes.VerifyCode,
        Routes.ResetPassword, Routes.PasswordChanged,
        Routes.EditProfile,
        Routes.Category,
        Routes.Detail // ✅ 서브카테고리 상세에선 하단바/FAB 숨김
    )

    // 상단/하단 모두 엣지-투-엣지
    val edgeToEdgeRoutes = setOf(
        Screen.Scan.route,
        Screen.Home.route,
        Screen.Forest.route,
        Screen.Community.route,
        Screen.Profile.route,
    )

    Scaffold(
        contentWindowInsets = when {
            currentRoute in edgeToEdgeRoutes -> WindowInsets(0, 0, 0, 0) // Scan 화면
            currentRoute in hideChromeRoutes -> WindowInsets(0, 0, 0, 0) // 풀스크린류
            else -> ScaffoldDefaults.contentWindowInsets
        },
        // 중앙 돌출 Scan 버튼
        floatingActionButton = {
            if (currentRoute !in hideChromeRoutes) {
                // FAB과 '흰 링'을 한 레이어(Box)에서 같이 이동시킴
                Box(
                    modifier = Modifier
                        .size(72.dp)        // 링 캔버스 영역(FAB보다 약간 큼)
                        .offset(y = 70.dp)  // 카메라 위치
                        .zIndex(2f),        // 하단바 위로
                    contentAlignment = Alignment.Center
                ) {
                    // 흰 링 (FAB 외곽)
                    Canvas(Modifier.matchParentSize()) {
                        val stroke = 4.dp.toPx()       // 링 두께 (원하면 3~5dp로 조절)
                        drawCircle(
                            color = Color.White,
                            style = Stroke(width = stroke)
                        )
                    }

                    FloatingActionButton(
                        onClick = {
                            if (currentRoute != Screen.Scan.route) {
                                navController.navigate(Screen.Scan.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        containerColor = GCTeal,
                        contentColor = Color.White,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp, pressedElevation = 0.dp,
                            focusedElevation = 0.dp, hoveredElevation = 0.dp
                        ),
                        modifier = Modifier.size(60.dp) // FAB 본체
                    ) {
                        Icon(
                            painter = painterResource(Screen.Scan.iconRes),
                            contentDescription = "Scan",
                            modifier = Modifier.size(FabIconSize)
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,


        bottomBar = {
            if (currentRoute !in hideChromeRoutes) {
                Column {
                    // ▲ 메뉴바 상단 구분선 (연한 회색)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE0E3E7)) // 필요하면 톤만 바꿔도 됨
                    )
                    Surface(
                        color = Color.White,                          // 배경 흰색 고정
                        tonalElevation = 0.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent        // Surface 색만 사용
                        ) {
                            bottomTabs.forEach { screen ->
                                val isScan = screen.route == Screen.Scan.route

                                if (isScan) {
                                    // 가운데 슬롯: 아이콘/라벨은 보이지 않게 하고 클릭만 활성화
                                    NavigationBarItem(
                                        icon = {
                                            // 공간 유지를 위해 투명 아이콘 배치
                                            Icon(
                                                painter = painterResource(Screen.Scan.iconRes),
                                                contentDescription = "Scan",
                                                modifier = Modifier.size(ScanSlotIconSize),
                                                tint = Color.Transparent
                                            )
                                        },
                                        label = null, // 라벨 숨김
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            if (currentRoute != Screen.Scan.route) {
                                                navController.navigate(Screen.Scan.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Transparent,
                                            selectedTextColor = Color.Transparent,
                                            unselectedIconColor = Color.Transparent,
                                            unselectedTextColor = Color.Transparent,
                                            indicatorColor = Color.Transparent
                                        )
                                    )
                                } else {
                                    // 일반 탭
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                painter = painterResource(screen.iconRes),
                                                modifier = Modifier.size(BottomBarIconSize),
                                                contentDescription = screen.label
                                            )
                                        },
                                        label = { Text(screen.label) },
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            if (currentRoute != screen.route) {
                                                // ✅ 현재 최상단이 로그인 전 Profile 화면이면 먼저 제거
                                                if (currentRoute == Screen.Profile.route) {
                                                    navController.popBackStack(Screen.Profile.route, inclusive = true)
                                                }
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = GCTeal,     // #008080
                                            selectedTextColor = GCTeal,
                                            unselectedIconColor = GCGray,   // #484C52
                                            unselectedTextColor = GCGray,
                                            indicatorColor = Color.Transparent
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    ) { innerPadding ->

    NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route)      { HomeScreen(navController) }
            composable(Screen.Forest.route) {
                // ✅ Scan 결과 누적치(leaf 촬영 횟수)를 Forest에 넘김
                ForestScreen(shots = shots, leafs = totalLeafs)
            }

        composable(Screen.Scan.route)      { ScanScreen(navController) }
            composable(Screen.Community.route) {
                val vm: CommunityViewModel = hiltViewModel()
                CommunityScreen(
                    viewModel = vm,
                    onNavigateProfile = { navController.navigate(Screen.Profile.route) }
                )
            }

            // 탭의 Profile 누르면 "로그인 전" 화면(= ProfileScreen) 노출
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onClickSignUp = { navController.navigate(Routes.SignUp) },
                    onForgotPassword = { navController.navigate(Routes.ForgotPassword) },
                    // 로그인 성공 시 아래로 내비게이트(연동 시 onLogin 콜백에서 호출)
                    onLogin = { _, _, _ ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Profile.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // 로그인 후 프로필 홈
            composable(Routes.ProfileHome) {
                ProfileHomeScreen(
                    onEditProfile = {
                        navController.navigate(Routes.EditProfile)
                    }
                )
            }

            // Edit Profile (풀스크린)
            composable(Routes.EditProfile) {
                EditProfileScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            //카테고리
            composable(
                route = Routes.Category,
                arguments = listOf(navArgument("categoryName") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                CategoryScreen(
                    navController   = navController,
                    categoryName    = categoryName
                )
            }

            // 🔹 상세
            composable(
                route = Routes.Detail,
                arguments = listOf(
                    navArgument("key") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val key  = backStackEntry.arguments?.getString("key") ?: ""
                val name = backStackEntry.arguments?.getString("name") ?: ""
                SubCategoryDetailScreen(
                    key = key,
                    name = name,
                    onBack = { navController.popBackStack() }
                )
            }

            // 🔹 회원가입 화면
            composable(Routes.SignUp) {
                SignUpScreen(
                    onBack = { navController.popBackStack() },
                    onSignUpSuccess = {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Profile.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onClickLogin = { navController.popBackStack() }
                )
            }


            // 🔹 Forgot Password: 이메일 입력
            composable(Routes.ForgotPassword) {
                ForgotPasswordScreen(
                    onBack = { navController.popBackStack() },
                    onSendCode = { email ->
                        // TODO: API 요청 후 성공 시
                        navController.navigate(Routes.VerifyCode)
                    },
                    onClickLogin = { navController.popBackStack() }
                )
            }

            // 🔹 Verify Code: 4자리 코드 입력
            composable(Routes.VerifyCode) {
                VerifyCodeScreen(
                    onBack = { navController.popBackStack() },
                    onVerified = {
                        navController.navigate(Routes.ResetPassword)
                    }
                )
            }

            // 🔹 Reset Password: 새 비밀번호
            composable(Routes.ResetPassword) {
                ResetPasswordScreen(
                    onBack = { navController.popBackStack() },
                    onResetDone = {
                        navController.navigate(Routes.PasswordChanged) {
                            popUpTo(Routes.ForgotPassword) { inclusive = true }
                        }
                    },
                    onClickLogin = {
                        // 완료 후 로그인으로 돌아가고 싶을 때
                        navController.popBackStack(Screen.Profile.route, inclusive = false)
                    }
                )
            }

            // 🔹 완료 화면
            composable(Routes.PasswordChanged) {
                PasswordChangedScreen(
                    onBackToLogin = {
                        // 프로필(로그인) 화면으로
                        navController.popBackStack(Screen.Profile.route, inclusive = false)
                    }
                )
            }

        }
    }
}