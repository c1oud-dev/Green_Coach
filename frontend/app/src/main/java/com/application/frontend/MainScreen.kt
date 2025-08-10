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
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.application.frontend.navigation.Screen
import com.application.frontend.ui.screen.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    val tabs = listOf(
        Screen.Home,
        Screen.Forest,
        Screen.Scan,
        Screen.Community,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route)      { HomeScreen(navController) }
            composable(Screen.Forest.route)    { ForestScreen() }
            composable(Screen.Scan.route)      { ScanScreen() }
            composable(Screen.Community.route) { CommunityScreen() }
            composable(Screen.Profile.route)   { ProfileScreen() }

            composable(
                route = "category/{categoryName}",
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
        }
    }
}