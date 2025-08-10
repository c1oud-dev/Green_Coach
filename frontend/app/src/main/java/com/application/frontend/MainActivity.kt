package com.application.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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