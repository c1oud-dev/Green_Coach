package com.application.frontend.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.frontend.R
import com.application.frontend.viewmodel.LoginUiState
import com.application.frontend.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogin: (email: String, password: String, rememberMe: Boolean) -> Unit = { _, _, _ -> },
    onForgotPassword: () -> Unit = {},
    onClickNaver: () -> Unit = {},
    onClickGoogle: () -> Unit = {},
    onClickSignUp: () -> Unit = {},
    // 🔹 추가: ViewModel 주입 (Hilt)
    viewModel: ProfileViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    // UI 상태
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

    // 🔹 추가: UI 피드백용 상태
    val uiState by viewModel.uiState.collectAsState()
    val snackHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()



    // 🔹 추가: 성공 시 상위(onLogin) 콜백 호출 → 기존 Nav 흐름 재사용
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> {
                onLogin(email.trim(), password, rememberMe)
                viewModel.clearState() // 상태 초기화(중복 네비 방지)
            }
            is LoginUiState.Error -> {
                scope.launch {
                    val msg = (uiState as LoginUiState.Error).message
                    snackHost.showSnackbar(message = msg)
                }
            }
            else -> Unit
        }
    }

    val isLoading = uiState is LoginUiState.Loading
    val canSubmit = email.isNotBlank() && password.isNotBlank() && !isLoading

    // 브랜드 컬러 (디자인 스샷 기준)
    val brandTeal = Color(0xFF0B8A80) // #008080 근처 톤
    val fieldShape = RoundedCornerShape(12.dp)

    Surface(Modifier.fillMaxSize(), color = Color.White) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // 상단 타이틀
                Text(
                    text = "로그인 후 이용해주세요",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3C2F2F) // 다크 브라운 느낌
                )

                Spacer(Modifier.height(24.dp))

                // Email
                Text(text = "Email address", fontSize = 14.sp, color = Color(0xFF5F5F5F))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Your email") },
                    singleLine = true,
                    shape = fieldShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                Spacer(Modifier.height(20.dp))

                // Password
                Text(text = "Password", fontSize = 14.sp, color = Color(0xFF5F5F5F))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password") },
                    singleLine = true,
                    shape = fieldShape,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default,
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Remember / Forgot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = brandTeal)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Remember me")
                    }
                    Text(
                        "Forgot password?",
                        color = Color(0xFF4E7C7A),
                        modifier = Modifier.clickable { onForgotPassword() }
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Login 버튼
                Button(
                    onClick = { viewModel.login(email.trim(), password) }, // 🔹 변경: VM 호출
                    enabled = canSubmit,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("login_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Log in", fontSize = 18.sp, color = Color.White)
                }

                Spacer(Modifier.height(50.dp))

                // Divider + Or with
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(Modifier.weight(1f))
                    Text(
                        text = "  Or with  ",
                        color = Color(0xFF9A9A9A),
                        textAlign = TextAlign.Center
                    )
                    Divider(Modifier.weight(1f))
                }

                Spacer(Modifier.height(16.dp))

                // 소셜 버튼 2개 (네이버 / 구글)
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1EC800)) // 네이버 컬러 텍스트
                    ) {

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painterResource(R.drawable.ic_naver),
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )

                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Naver")
                    }

                    OutlinedButton(
                        onClick = onClickGoogle,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        val googleIcon =
                            runCatching { painterResource(id = R.drawable.ic_google) }.getOrNull()
                        if (googleIcon != null) {
                            Image(
                                painter = googleIcon,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("Google")
                    }
                }

                Spacer(Modifier.height(70.dp))

                // 회원가입 유도
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Don’t have an account? ")
                    Text(
                        text = "Sign up",
                        color = brandTeal,
                        modifier = Modifier.clickable { onClickSignUp() }
                    )
                }
            }
            // 🔹 스낵바 호스트 — 화면 맨 아래쪽
            SnackbarHost(
                hostState = snackHost,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}