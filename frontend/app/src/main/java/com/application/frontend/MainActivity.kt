package com.application.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.frontend.ui.screen.CategoryScreen
import com.application.frontend.ui.screen.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 상태바/내비게이션바 투명 + 컨텐츠 엣지-투-엣지
        enableEdgeToEdge()

        // ✅ 상태바 아이콘 색 (헤더가 진하면 false → 흰 아이콘)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            // 필요하면 내비게이션바 아이콘도: isAppearanceLightNavigationBars = true/false
        }

        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    Surface {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable(
                route = "category/{name}"
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                CategoryScreen(
                    navController    = navController,
                    categoryName     = name
                )
            }
        }
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme   = MaterialTheme.colorScheme,
        typography    = MaterialTheme.typography,
        content       = content
    )
}