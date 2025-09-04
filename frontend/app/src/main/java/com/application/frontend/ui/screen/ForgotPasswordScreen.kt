package com.application.frontend.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit = {},
    onSendCode: (email: String) -> Unit = {},
    onClickLogin: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    val brandTeal = Color(0xFF0B8A80)

    Surface(Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 12.dp)
        ) {
            // 상단 Back 아이콘
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 4.dp, bottom = 60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back"
                )
            }

            Spacer(Modifier.height(50.dp))
            Text("Forgot password?", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3C2F2F))
            Spacer(Modifier.height(8.dp))
            Text(
                "Don’t worry! It happens. Please enter the email associated with your account.",
                color = Color(0xFF666666)
            )

            Spacer(Modifier.height(24.dp))
            Text("Email address", color = Color(0xFF5F5F5F))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                placeholder = { Text("Enter your email address") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onSendCode(email.trim()) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = brandTeal),
                shape = MaterialTheme.shapes.medium
            ) { Text("Send code", color = Color.White, fontSize = 18.sp) }

            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Remember password? ")
                Text("Log in", color = brandTeal, modifier = Modifier.clickable { onClickLogin() })
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
