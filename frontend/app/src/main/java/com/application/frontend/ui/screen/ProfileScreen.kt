package com.application.frontend.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import com.application.frontend.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogin: (email: String, password: String, rememberMe: Boolean) -> Unit = { _, _, _ -> },
    onForgotPassword: () -> Unit = {},
    onClickNaver: () -> Unit = {},
    onClickGoogle: () -> Unit = {},
    onClickSignUp: () -> Unit = {},
    viewModel: LoginViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    // 🔹 UI 피드백용 상태
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onLogin(uiState.email.trim(), uiState.password, uiState.rememberMe)
            viewModel.consumeSuccess()
        }
    }

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
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
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
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChanged,
                    placeholder = { Text("Password") },
                    singleLine = true,
                    shape = fieldShape,
                    visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default,
                    trailingIcon = {
                        IconButton(onClick = viewModel::onPasswordVisibilityToggled) {
                            Icon(
                                imageVector = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
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

                uiState.errorMessage?.let {
                    Text(
                        text = it,
                        color = Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Remember / Forgot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.rememberMe,
                            onCheckedChange = viewModel::onRememberMeChanged,
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
                    onClick = viewModel::login,
                    enabled = uiState.canSubmit,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("login_button")
                ) {
                    if (uiState.isLoading) {
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
                    Text("  Or with  ", color = Color(0xFF9A9A9A))
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
                        Image(
                            painter = painterResource(R.drawable.ic_naver),
                            contentDescription = "Naver",
                            modifier = Modifier.size(15.dp)
                        )
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
                        val googleIcon = runCatching { painterResource(id = R.drawable.ic_google) }.getOrNull()
                        if (googleIcon != null) {
                            Image(
                                painter = googleIcon,
                                contentDescription = "Google",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("Google")
                    }
                }

                Spacer(Modifier.height(24.dp))

                // 회원가입 유도
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Don't have an account?",
                        fontSize = 14.sp,
                        color = Color(0xFF6D6D6D)
                    )
                    Text(
                        "Sign up",
                        color = brandTeal,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { onClickSignUp() }
                    )
                }
            }

        }
    }
}