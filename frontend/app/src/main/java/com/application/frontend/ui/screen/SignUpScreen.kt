package com.application.frontend.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.application.frontend.viewmodel.NicknameCheckResult
import com.application.frontend.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    onBack: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {},
    onClickNaver: () -> Unit = {},
    onClickGoogle: () -> Unit = {},
    onClickLogin: () -> Unit = onBack,
    viewModel: SignUpViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onSignUpSuccess()
            viewModel.consumeSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage ?: return@LaunchedEffect
        scope.launch { snackHost.showSnackbar(message) }
        viewModel.consumeError()
    }

    val brandTeal = Color(0xFF0B8A80)
    val fieldShape = RoundedCornerShape(12.dp)

    Surface(Modifier.fillMaxSize(), color = Color.White) {
        Box(Modifier.fillMaxSize()) {
            SnackbarHost(hostState = snackHost, modifier = Modifier.align(Alignment.BottomCenter))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 5.dp),
                horizontalAlignment = Alignment.Start
            ) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = uiState.nickname,
                        onValueChange = viewModel::onNicknameChanged,
                        placeholder = { Text("nickname") },
                        singleLine = true,
                        shape = fieldShape,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("signup_nickname"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE0E0E0),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = viewModel::checkNickname,
                        enabled = uiState.canRequestNicknameCheck,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brandTeal)
                    ) {
                        if (uiState.isCheckingNickname) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("중복 확인", color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))
                when (uiState.nicknameCheckResult) {
                    NicknameCheckResult.AVAILABLE ->
                        Text("사용 가능한 닉네임입니다.", color = Color(0xFF1B8A2C), fontSize = 12.sp)
                    NicknameCheckResult.UNAVAILABLE ->
                        Text("이미 사용 중인 닉네임입니다.", color = Color(0xFFD32F2F), fontSize = 12.sp)
                    null -> Unit
                }

                Spacer(Modifier.height(16.dp))

                Text("Email", fontSize = 14.sp, color = Color(0xFF5F5F5F))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
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
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChanged,
                    placeholder = { Text("must be 8 characters") },
                    singleLine = true,
                    shape = fieldShape,
                    visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = viewModel::togglePasswordVisibility) {
                            Icon(
                                imageVector = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
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
                    onClick = viewModel::signUp,
                    enabled = uiState.canSubmit,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("signup_button")
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                    }
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
            }

        }
    }
}
