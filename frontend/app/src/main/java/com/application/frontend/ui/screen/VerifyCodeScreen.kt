package com.application.frontend.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun VerifyCodeScreen(
    onBack: () -> Unit = {},
    onVerified: () -> Unit = {}
) {
    val brandTeal = Color(0xFF0B8A80)
    val boxShape = RoundedCornerShape(12.dp)

    // 4자리 코드 상태
    var code0 by remember { mutableStateOf("") }
    var code1 by remember { mutableStateOf("") }
    var code2 by remember { mutableStateOf("") }
    var code3 by remember { mutableStateOf("") }

    val code = code0 + code1 + code2 + code3
    val canVerify = code.length == 4

    // 20초 카운트다운
    var secondsLeft by remember { mutableStateOf(20) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    @Composable
    fun box(text: String, onClick: () -> Unit) = Box(
        modifier = Modifier
            .size(width = 64.dp, height = 58.dp)
            .border(1.dp, Color(0xFFE0E0E0), boxShape)
            .background(Color.White, boxShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 20.sp)
    }

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
            Text("Please check your email", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3C2F2F))
            Spacer(Modifier.height(4.dp))
            Text("We’ve sent a code to helloworld@gmail.com", color = Color(0xFF666666))

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                box(code0) { /* 포커싱 대신 간단 입력 다이얼로그/키패드 연결 가능 */ }
                box(code1) { }
                box(code2) { }
                box(code3) { }
            }

            // 🔹 데모 입력용 키패드 대체 (실제에선 각 박스에 TextField 적용 권장)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = code,
                onValueChange = {
                    val onlyDigits = it.filter { ch -> ch.isDigit() }.take(4)
                    code0 = onlyDigits.getOrNull(0)?.toString() ?: ""
                    code1 = onlyDigits.getOrNull(1)?.toString() ?: ""
                    code2 = onlyDigits.getOrNull(2)?.toString() ?: ""
                    code3 = onlyDigits.getOrNull(3)?.toString() ?: ""
                },
                placeholder = { Text("Enter 4-digit code") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { if (canVerify) onVerified() },
                enabled = canVerify,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = brandTeal, disabledContainerColor = Color(0xFFB2DFDB)),
                shape = MaterialTheme.shapes.medium
            ) { Text("Verify", color = Color.White, fontSize = 18.sp) }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "Send code again  00:${secondsLeft.toString().padStart(2,'0')}",
                color = Color(0xFF666666),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVerifyCodeScreen() {
    VerifyCodeScreen(
        onBack = {},
        onVerified = {}
    )
}
