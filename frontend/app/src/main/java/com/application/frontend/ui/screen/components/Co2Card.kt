package com.application.frontend.ui.screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.application.frontend.model.Co2Snapshot

@Composable
fun Co2Card(
    world: Co2Snapshot?,
    korea: Co2Snapshot?,
    modifier: Modifier = Modifier,                     // ← modifier를 첫 번째 선택 인자로
    meValue: Pair<Double, Double> = 0.0 to 0.0        // ← 나머지 선택 인자 뒤로
) {
    // ✅ 무자료 가드 — 카드 형태로 안내 노출 후 즉시 반환
    val noData =
        (world?.emissions?.points.isNullOrEmpty() && korea?.emissions?.points.isNullOrEmpty()
                && meValue.first == 0.0 && meValue.second == 0.0)

    if (noData) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    "CO₂ 데이터를 불러오는 중이거나 이용할 수 없습니다.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        return   // ⬅️ Composable 종료
    }

    val surface = Color.White
    val emissionsColor = Color(0xFF4CAF50)   // 초록 (배출)
    val reductionColor = Color(0xFFFF8A65)   // 오렌지 (감소)
    val trackColor = Color(0xFFE9EEF3)       // 연한 트랙 배경

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(18.dp)     // ✅ 둥근 카드
    ) {
        Column(Modifier.padding(horizontal = 27.dp, vertical = 25.dp)) {
            Text("CO2 Emissions & Reduction", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(15.dp))

            // ✅ 레전드 점
            Row(verticalAlignment = Alignment.CenterVertically) {
                LegendDot(color = emissionsColor); Spacer(Modifier.width(8.dp))
                Text("Emissions", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(16.dp))
                LegendDot(color = reductionColor); Spacer(Modifier.width(8.dp))
                Text("Reduction", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(25.dp))

            // ✅ 상대 스케일 사용 여부(원한다면 false로 되돌릴 수 있음)
            val useRelativeScale = true

            // 각 그룹별 최대값 계산
            val maxWorld = listOfNotNull(
                world?.emissions?.points?.maxOfOrNull { it.value },
                world?.reduction?.points?.maxOfOrNull { it.value }
            ).maxOrNull() ?: 1.0
            val maxKorea = listOfNotNull(
                korea?.emissions?.points?.maxOfOrNull { it.value },
                korea?.reduction?.points?.maxOfOrNull { it.value }
            ).maxOrNull() ?: 1.0
            val maxMe = maxOf(meValue.first, meValue.second, 1.0)

            // 최소 막대 높이(실데이터가 0보다 크면 시각적으로 보이게 바닥 높이 적용)
            val minBar = 3f  // px

            // 추가: Y축 폭(라벨 영역)
            val yAxisWidth = 28.dp

            // (선택) 월드 기준 눈금 생성 (1k ~ 7k)
            fun roundUpToK(v: Double): Int = (((v + 999.0) / 1000).toInt()) * 1000
            val yMaxK = roundUpToK(maxWorld)
            val yTicks = listOf(1,2,3,4,5,6,7).map { it * 1000 }.filter { it <= maxOf(7000, yMaxK) }.takeLast(7)

            Row(Modifier.fillMaxWidth().height(170.dp)) {
                // ── 왼쪽: Y축 숫자
                Column(
                    Modifier.width(yAxisWidth).fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    yTicks.reversed().forEach { v ->
                        Text("${v/1000}k", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // ── 오른쪽: 실제 그래프 캔버스
                Canvas(Modifier.fillMaxSize()) {
                    val groups = 3
                    val groupWidth = size.width / groups
                    val barWidth = groupWidth / 7f
                    val gap = 16f
                    val topPadding = 10f
                    val bottomPadding = 18f
                    val trackHeight = size.height - topPadding - bottomPadding
                    val base = topPadding + trackHeight
                    val radius = 16f
                    val minBar = 6f

                    fun h(v: Double?, m: Double): Float {
                        val scaled = ((v ?: 0.0) / m).toFloat() * trackHeight
                        return if ((v ?: 0.0) > 0.0) kotlin.math.max(scaled, minBar) else 0f
                    }

                    fun drawGroup(idx: Int, e: Double?, r: Double?, groupMax: Double) {
                        val centerX = idx * groupWidth + groupWidth / 2f
                        val eX = centerX - barWidth - gap / 2f
                        val rX = centerX + gap / 2f

                        // 1) 배경 트랙(연한 기둥)
                        drawRoundRect(
                            color = trackColor,
                            topLeft = Offset(eX, topPadding),
                            size = Size(barWidth, trackHeight),
                            cornerRadius = CornerRadius(radius, radius)
                        )
                        drawRoundRect(
                            color = trackColor,
                            topLeft = Offset(rX, topPadding),
                            size = Size(barWidth, trackHeight),
                            cornerRadius = CornerRadius(radius, radius)
                        )

                        // 2) 실제 값 채우기(아래→위)
                        val eH = h(e, groupMax)
                        val rH = h(r, groupMax)

                        drawRoundRect(
                            color = emissionsColor,
                            topLeft = Offset(eX, base - eH),
                            size = Size(barWidth, eH),
                            cornerRadius = CornerRadius(radius, radius)
                        )
                        drawRoundRect(
                            color = reductionColor,
                            topLeft = Offset(rX, base - rH),
                            size = Size(barWidth, rH),
                            cornerRadius = CornerRadius(radius, radius)
                        )
                    }

                    // 그룹별 상대 스케일
                    drawGroup(
                        0,
                        world?.emissions?.points?.lastOrNull()?.value,
                        world?.reduction?.points?.lastOrNull()?.value,
                        maxWorld
                    )
                    drawGroup(
                        1,
                        korea?.emissions?.points?.lastOrNull()?.value,
                        korea?.reduction?.points?.lastOrNull()?.value,
                        maxKorea
                    )
                    drawGroup(2, meValue.first, meValue.second, maxMe)
                }
            }

            Spacer(Modifier.height(6.dp))
            // 변경 후: Y축 폭만큼 띄우고, 그룹별 중앙 정렬
            Row(Modifier.fillMaxWidth()) {
                Spacer(Modifier.width(yAxisWidth))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { Text("World", style = MaterialTheme.typography.bodySmall) }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { Text("Korea", style = MaterialTheme.typography.bodySmall) }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { Text("Me", style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color) {
    Box(
        Modifier.size(10.dp).background(color = color, shape = CircleShape)
    )
}