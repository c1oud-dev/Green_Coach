package com.application.frontend.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit = {},
    onResetDone: () -> Unit = {},
    onClickLogin: () -> Unit = {}
) {
    val brandTeal = Color(0xFF0B8A80)
    var pw by remember { mutableStateOf("") }
    var pw2 by remember { mutableStateOf("") }
    var v1 by remember { mutableStateOf(false) }
    var v2 by remember { mutableStateOf(false) }
    val enabled = pw.length >= 8 && pw == pw2

    Surface(Modifier.fillMaxSize(), color = Color.White) {
        Column(
            Modifier
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
            Text("Reset password", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3C2F2F))
            Spacer(Modifier.height(4.dp))
            Text("Please type something you’ll remember", color = Color(0xFF666666))

            Spacer(Modifier.height(24.dp))
            Text("New password", color = Color(0xFF5F5F5F))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = pw, onValueChange = { pw = it },
                placeholder = { Text("must be 8 characters") },
                singleLine = true,
                visualTransformation = if (v1) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { v1 = !v1 }) {
                        Icon(if (v1) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Text("Confirm new password", color = Color(0xFF5F5F5F))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = pw2, onValueChange = { pw2 = it },
                placeholder = { Text("repeat password") },
                singleLine = true,
                visualTransformation = if (v2) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { v2 = !v2 }) {
                        Icon(if (v2) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onResetDone,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = brandTeal, disabledContainerColor = Color(0xFFB2DFDB)),
                shape = MaterialTheme.shapes.medium
            ) { Text("Reset password", color = Color.White, fontSize = 18.sp) }

            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Already have an account? ")
                Text("Log in", color = brandTeal, modifier = Modifier.clickable { onClickLogin() })
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewResetPasswordScreen() {
    ResetPasswordScreen(
        onBack = {},
        onResetDone = {},
        onClickLogin = {}
    )
}
