package com.application.frontend.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

// 🔹 바텀 탭
sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home      : Screen("home",      Icons.Filled.Home,       "Home")
    object Forest    : Screen("forest",    Icons.Filled.Nature,     "Forest")
    object Scan      : Screen("scan",      Icons.Filled.CameraAlt,  "")
    object Community : Screen("community", Icons.Filled.People,     "Community")
    object Profile   : Screen("profile",   Icons.Filled.Person,     "Profile")
}

// 🔹 일반 화면(상세/카테고리 등) 라우트 모음
object Routes {
    // MainScreen에 이미 쓰는 파라미터명에 맞춤
    const val Category = "category/{categoryName}"

    // 서브카테고리 상세
    const val Detail   = "detail/{key}/{name}"

    fun category(categoryName: String) =
        "category/${Uri.encode(categoryName)}"

    fun detail(key: String, name: String) =
        "detail/$key/${Uri.encode(name)}"
}