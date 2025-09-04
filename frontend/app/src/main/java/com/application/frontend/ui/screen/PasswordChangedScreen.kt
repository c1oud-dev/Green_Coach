package com.application.frontend.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PasswordChangedScreen(
    onBackToLogin: () -> Unit = {}
) {
    val brandTeal = Color(0xFF0B8A80)

    Surface(Modifier.fillMaxSize(), color = Color.White) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Password changed", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3C2F2F))
            Spacer(Modifier.height(8.dp))
            Text("Your password has been changed successfully", color = Color(0xFF666666))
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onBackToLogin,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = brandTeal)
            ) { Text("Back to login", color = Color.White, fontSize = 18.sp) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordChangedScreen() {
    PasswordChangedScreen(
        onBackToLogin = {}
    )
}
