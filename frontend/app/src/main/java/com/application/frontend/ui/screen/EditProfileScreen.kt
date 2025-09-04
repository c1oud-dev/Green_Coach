package com.application.frontend.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.frontend.R

data class EditProfileUi(
    val nickname: String,
    val email: String,
    val avatarRes: Int? = null,
    val birth: String? = null,   // 미리보기 단순화를 위해 String
    val gender: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    preset: EditProfileUi = EditProfileUi(nickname = "", email = ""),
    onBack: () -> Unit = {},
    onSaved: (EditProfileUi) -> Unit = {}
) {
    val brandTeal = Color(0xFF0B8A80)

    var nickname by remember { mutableStateOf(preset.nickname) }
    var birth by remember { mutableStateOf(preset.birth) }
    val email = preset.email // 수정 불가
    var gender by remember { mutableStateOf(preset.gender) }

    var genderMenuExpanded by remember { mutableStateOf(false) }

    Surface(Modifier.fillMaxSize(), color = Color.White) {
        Column(Modifier.fillMaxSize()) {

            // 상단 바
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "back") }
                Text("Edit Profile", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            }

            // 아바타
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val res = preset.avatarRes) {
                    null -> Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .size(118.dp)
                            .clip(CircleShape)
                    )
                    else -> Image(
                        painter = painterResource(res),
                        contentDescription = "avatar",
                        modifier = Modifier
                            .size(118.dp)
                            .clip(CircleShape)
                    )
                }
                Box(modifier = Modifier.padding(top = 6.dp)) {
                    AssistChip(onClick = { /* TODO: 이미지 변경 */ }, label = { Text("Change photo") })
                }
            }

            Spacer(Modifier.height(15.dp))

            // 폼
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp)
            ) {
                // 닉네임 (수정 가능)
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("Nickname") }
                )

                Spacer(Modifier.height(12.dp))

                // 생년월일 (선택)
                OutlinedTextField(
                    value = birth.orEmpty(),
                    onValueChange = { birth = it }, // 추후 DatePicker 연결
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("Date of birth") },
                    visualTransformation = VisualTransformation.None
                )

                Spacer(Modifier.height(12.dp))

                // 이메일 (수정 불가)
                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("Email") }
                )

                Spacer(Modifier.height(15.dp))

                // Gender (선택)
                ExposedDropdownMenuBox(
                    expanded = genderMenuExpanded,
                    onExpandedChange = { genderMenuExpanded = !genderMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = gender ?: "Gender",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = genderMenuExpanded,
                        onDismissRequest = { genderMenuExpanded = false }
                    ) {
                        listOf("Male", "Female", "Other", "Prefer not to say").forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    gender = it
                                    genderMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(120.dp))

                OutlinedButton(
                    onClick = {
                        onSaved(
                            preset.copy(
                                nickname = nickname,
                                birth = birth,
                                gender = gender
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    border = ButtonDefaults.outlinedButtonBorder,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = brandTeal),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Logout") }
            }
        }
    }
}

/* ---------------- Preview 전용 샘플 (런타임 X) ---------------- */

@Preview(showBackground = true)
@Composable
private fun Preview_EditProfileScreen() {
    EditProfileScreen(
        preset = EditProfileUi(
            nickname = "Albert Flores",
            email = "albertflores@mail.com",
            avatarRes = R.drawable.ic_avatar_default,
            birth = "01/01/1988",
            gender = "Male"
        )
    )
}
