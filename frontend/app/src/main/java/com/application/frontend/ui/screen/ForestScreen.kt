package com.application.frontend.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import com.application.frontend.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.frontend.model.ForestStage
import com.application.frontend.ui.screen.components.Co2Card
import com.application.frontend.ui.screen.components.ForestProgressSection
import com.application.frontend.viewmodel.ForestViewModel

import androidx.compose.ui.tooling.preview.Preview
import com.application.frontend.model.Co2Point
import com.application.frontend.model.Co2Series
import com.application.frontend.model.Co2Snapshot

private val ForestBg = Color(0xFFD7E9D4) // 연한 민트톤 배경
@Composable
fun ForestScreen(
    // ScanScreen에서 공유한 누적 촬영 수. (임시 0 기본값)
    shots: Int = 0,
    viewModel: ForestViewModel = hiltViewModel()
) {
    LaunchedEffect(shots) { viewModel.init(shots) }
    val ui = viewModel.ui.collectAsState().value

    Column(
        Modifier
            .fillMaxSize()
            .background(ForestBg)                 // ✅ 배경색
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))
        Text(
            text = ui.stage.title, // "Seed" ~ "Mature Tree"
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,         // ✅ 좀 더 굵게
        )
        Spacer(Modifier.height(25.dp))

        // 단계별 이미지 교체
        val img = when {
            // 새싹 단계 + 8회 이상이면 더 자란 새싹 이미지
            ui.stage == ForestStage.SPROUT && ui.shots >= 8 -> R.drawable.sprout_grown

            // 기본 매핑
            ui.stage == ForestStage.SEED   -> R.drawable.seed
            ui.stage == ForestStage.SPROUT -> R.drawable.sprout
            ui.stage == ForestStage.SAPLING -> R.drawable.sapling
            ui.stage == ForestStage.GROWING -> R.drawable.growing_tree
            ui.stage == ForestStage.MATURE  -> R.drawable.mature_tree
            ui.stage == ForestStage.FRUIT   -> R.drawable.fruit_tree
            else -> R.drawable.seed
        }
        Image(
            painter = painterResource(img),
            contentDescription = ui.stage.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Spacer(Modifier.height(20.dp))

        ForestProgressSection(
            title = "Progress",
            percent = ui.progressPercent,
            subtitle = when (ui.stage) {
                ForestStage.SEED   -> "환영해요! 쓰레기 사진을 찍고 나무를 키워보세요."
                ForestStage.SPROUT -> "잘했어요! 새싹이 돋아났어요."
                ForestStage.SAPLING -> "좋아요! 어린 나무로 자랐어요."
                ForestStage.GROWING -> "거의 다 왔어요! 무럭무럭 자라는 중."
                ForestStage.MATURE  -> "훌륭해요! 성숙한 나무가 되었어요."
                ForestStage.FRUIT   -> "최고예요! 열매를 맺는 나무가 되었어요."
            },
            onHelpClick = { viewModel.toggleHelp(true) }
        )

        Spacer(Modifier.height(35.dp))

        Co2Card(
            world = ui.world,
            korea = ui.korea,
            modifier = Modifier.fillMaxWidth(),    // ✅ 가로 꽉
            meValue = (ui.shots.toDouble() to (ui.shots * 0.2)) // 예시: 촬영수 기반 간단 감소량
        )
    }

    if (ui.helpDialog) {
        GrowthHelpDialog(onDismiss = { viewModel.toggleHelp(false) })
    }
}

@Composable
private fun GrowthHelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("숲 성장 단계", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("1. 씨앗 단계 (Seed)\n- 쓰레기 촬영 1~5회 달성 시.\n")
                Text("2. 새싹 단계 (Sprout)\n- 쓰레기 촬영 6~10회 달성 시.\n")
                Text("3. 어린 나무 단계 (Sapling)\n- 쓰레기 촬영 11~15회 달성 시.\n")
                Text("4. 성장 중인 나무 단계 (Growing Tree)\n- 쓰레기 촬영 16~20회 달성 시.\n")
                Text("5. 성숙한 나무 단계 (Mature Tree)\n- 쓰레기 촬영 21~30회 달성 시.\n")
                Text("6. 열매 맺은 나무 단계 (Fruit-bearing Tree)\n- 쓰레기 촬영 31회 이상 달성 시.\n")
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("확인") } }
    )
}


/* ─────────────────────────────────────────────────────────────
   ForestScreen 프리뷰용 호스트
   - Hilt / ViewModel 없이 UI만 렌더링
   - 실제 ForestScreen과 동일한 레이아웃/컴포넌트 사용
   ───────────────────────────────────────────────────────────── */

// 1) 프리뷰에 쓸 UI 축약 모델 (ForestViewModel의 ui와 동일 필드만)
private data class PreviewForestUi(
    val stage: ForestStage,
    val shots: Int,
    val progressPercent: Int,
    val world: Co2Snapshot?,
    val korea: Co2Snapshot?,
    val helpDialog: Boolean = false
)

// 2) 프리뷰용 렌더러: ForestScreen과 동일한 본문을 그대로 재사용
@Composable
private fun ForestScreenPreviewHost(ui: PreviewForestUi) {
    Column(
        Modifier
            .fillMaxSize()
            .background(ForestBg)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = ui.stage.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))

        // 단계별 이미지 매핑 (ForestScreen과 동일)
        val img = when {
            ui.stage == ForestStage.SPROUT && ui.shots >= 8 -> R.drawable.sprout_grown
            ui.stage == ForestStage.SEED    -> R.drawable.seed
            ui.stage == ForestStage.SPROUT  -> R.drawable.sprout
            ui.stage == ForestStage.SAPLING -> R.drawable.sapling
            ui.stage == ForestStage.GROWING -> R.drawable.growing_tree
            ui.stage == ForestStage.MATURE  -> R.drawable.mature_tree
            ui.stage == ForestStage.FRUIT   -> R.drawable.fruit_tree
            else -> R.drawable.seed
        }
        Image(
            painter = painterResource(img),
            contentDescription = ui.stage.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().height(180.dp)
        )

        Spacer(Modifier.height(12.dp))

        ForestProgressSection(
            title = "Progress",
            percent = ui.progressPercent,
            subtitle = when (ui.stage) {
                ForestStage.SEED   -> "환영해요! 쓰레기 사진을 찍고 나무를 키워보세요."
                ForestStage.SPROUT -> "잘했어요! 새싹이 돋아났어요."
                ForestStage.SAPLING -> "좋아요! 어린 나무로 자랐어요."
                ForestStage.GROWING -> "거의 다 왔어요! 무럭무럭 자라는 중."
                ForestStage.MATURE  -> "훌륭해요! 성숙한 나무가 되었어요."
                ForestStage.FRUIT   -> "최고예요! 열매를 맺는 나무가 되었어요."
            },
            onHelpClick = { /* preview: no-op */ }
        )

        Spacer(Modifier.height(35.dp))

        Co2Card(
            world = ui.world,
            korea = ui.korea,
            modifier = Modifier.fillMaxWidth(),
            meValue = (ui.shots.toDouble() to (ui.shots * 0.2))
        )
    }
}

/* 3) 샘플 데이터 유틸 (막대가 보이도록 간단 값) */
private fun sampleCo2(emissions: List<Double>, reductions: List<Double>): Co2Snapshot =
    Co2Snapshot(
        emissions = Co2Series("Emissions", emissions.mapIndexed { i, v -> Co2Point(2000 + i, v) }),
        reduction = Co2Series("Reduction", reductions.mapIndexed { i, v -> Co2Point(2000 + i, v) })
    )

private val worldSample by lazy {
    sampleCo2(
        emissions  = listOf(6000.0, 6500.0, 7000.0),
        reductions = listOf(3000.0, 3500.0, 4000.0)
    )
}

private val koreaSample by lazy {
    sampleCo2(
        emissions  = listOf(1500.0, 1800.0, 2200.0),
        reductions = listOf(1200.0, 1500.0, 1600.0)
    )
}

/* 4) 프리뷰 세트: Seed → Fruit */
@Preview(showBackground = true, name = "Forest · Seed")
@Composable private fun Preview_ForestScreen_Seed() {
    ForestScreenPreviewHost(
        ui = PreviewForestUi(
            stage = ForestStage.SEED,
            shots = 2,
            progressPercent = 5,
            world = worldSample,
            korea = koreaSample
        )
    )
}

@Preview(showBackground = true, name = "Forest · Sprout")
@Composable private fun Preview_ForestScreen_Sprout() {
    ForestScreenPreviewHost(
        ui = PreviewForestUi(
            stage = ForestStage.SPROUT,
            shots = 8,
            progressPercent = 20,
            world = worldSample,
            korea = koreaSample
        )
    )
}

@Preview(showBackground = true, name = "Forest · Sapling")
@Composable private fun Preview_ForestScreen_Sapling() {
    ForestScreenPreviewHost(
        ui = PreviewForestUi(
            stage = ForestStage.SAPLING,
            shots = 12,
            progressPercent = 40,
            world = worldSample,
            korea = koreaSample
        )
    )
}

@Preview(showBackground = true, name = "Forest · Growing")
@Composable private fun Preview_ForestScreen_Growing() {
    ForestScreenPreviewHost(
        ui = PreviewForestUi(
            stage = ForestStage.GROWING,
            shots = 18,
            progressPercent = 60,
            world = worldSample,
            korea = koreaSample
        )
    )
}

@Preview(showBackground = true, name = "Forest · Mature")
@Composable private fun Preview_ForestScreen_Mature() {
    ForestScreenPreviewHost(
        ui = PreviewForestUi(
            stage = ForestStage.MATURE,
            shots = 24,
            progressPercent = 85,
            world = worldSample,
            korea = koreaSample
        )
    )
}

@Preview(showBackground = true, name = "Forest · Fruit")
@Composable private fun Preview_ForestScreen_Fruit() {
    ForestScreenPreviewHost(
        ui = PreviewForestUi(
            stage = ForestStage.FRUIT,
            shots = 32,
            progressPercent = 100,
            world = worldSample,
            korea = koreaSample
        )
    )
}
