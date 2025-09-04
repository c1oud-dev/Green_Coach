package com.application.frontend.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.frontend.R

@Composable
fun SignUpScreen(
    onBack: () -> Unit = {},
    onSignUp: (nickname: String, email: String, password: String) -> Unit = { _,_,_ -> },
    onClickNaver: () -> Unit = {},
    onClickGoogle: () -> Unit = {},
    onClickLogin: () -> Unit = onBack, // "Log in" 텍스트 탭 시
) {
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }

    val brandTeal = Color(0xFF0B8A80)
    val fieldShape = RoundedCornerShape(12.dp)

    Surface(Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 5.dp),
            horizontalAlignment = Alignment.Start
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

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3C2F2F)
            )

            Spacer(Modifier.height(24.dp))

            Text("Nickname", fontSize = 14.sp, color = Color(0xFF5F5F5F))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                placeholder = { Text("nickname") },
                singleLine = true,
                shape = fieldShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_nickname"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(Modifier.height(16.dp))

            Text("Email", fontSize = 14.sp, color = Color(0xFF5F5F5F))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("example@gmail.com") },
                singleLine = true,
                shape = fieldShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_email"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(Modifier.height(16.dp))

            Text("Create a password", fontSize = 14.sp, color = Color(0xFF5F5F5F))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("must be 8 characters") },
                singleLine = true,
                shape = fieldShape,
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Icon(
                            imageVector = if (pwVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_password"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(Modifier.height(80.dp))

            Button(
                onClick = { onSignUp(nickname.trim(), email.trim(), password) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = brandTeal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("signup_button")
            ) {
                Text("Sign up", fontSize = 18.sp, color = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ")
                Text(
                    "Log in",
                    color = brandTeal,
                    modifier = Modifier.clickable { onClickLogin() }
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Divider(Modifier.weight(1f))
                Text("  Or with  ", color = Color(0xFF9A9A9A))
                Divider(Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onClickNaver,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1EC800))
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_naver),
                        contentDescription = "Naver",
                        modifier = Modifier
                            .size(15.dp)   // 🔥 아이콘 사이즈
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Naver") // 텍스트도 "Naver"로 바꿀 수 있음
                }

                OutlinedButton(
                    onClick = onClickGoogle,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    val googleIcon = runCatching { painterResource(id = R.drawable.ic_google) }.getOrNull()
                    if (googleIcon != null) {
                        Image(painter = googleIcon, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Google")
                }
            }

        }
    }
}
