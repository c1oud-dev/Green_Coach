package com.application.frontend

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.application.frontend.navigation.Routes
import com.application.frontend.navigation.Screen
import com.application.frontend.ui.screen.*
import com.application.frontend.viewmodel.CommunityViewModel
import com.application.frontend.viewmodel.ScanViewModel

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

    val tabs = listOf(
        Screen.Home,
        Screen.Forest,
        Screen.Scan,
        Screen.Community,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            // 🔹 회원가입/비번찾기 플로우에서는 탭바 숨김
            val hideBottomBarRoutes = setOf(
                Routes.SignUp, Routes.ForgotPassword, Routes.VerifyCode,
                Routes.ResetPassword, Routes.PasswordChanged,
                Routes.EditProfile
            )

            // 회원가입 화면일 때는 NavigationBar 숨김
            if (currentRoute !in hideBottomBarRoutes) {
                NavigationBar {
                    tabs.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
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
                ForestScreen(shots = shots)
            }

            composable(Screen.Scan.route)      { ScanScreen() }
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
                    onLogin = { _,_,_ -> navController.navigate(Routes.ProfileHome) }
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
                    onSignUp = { _, _, _ ->
                        // 회원가입 성공 시 프로필 홈으로 이동
                        navController.navigate(Routes.ProfileHome) {
                            popUpTo(Screen.Profile.route) { inclusive = false }
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