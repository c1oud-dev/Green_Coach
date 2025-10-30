package com.application.frontend.data.repository

import com.application.frontend.data.ScanApi
import com.application.frontend.data.local.ScanHistoryStorage
import com.application.frontend.ui.screen.ScanHistoryDto
import com.application.frontend.ui.screen.ScanResultDto
import okhttp3.MultipartBody
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

interface ScanRepository {
    suspend fun analyzeImage(imagePart: MultipartBody.Part): ScanResultDto
    suspend fun getScanHistory(): List<ScanHistoryDto>
    suspend fun saveScanResult(result: ScanResultDto): ScanHistoryDto
    suspend fun getLocalHistory(): List<ScanHistoryDto>
}

@Singleton
class ScanRepositoryImpl @Inject constructor(
    private val scanApi: ScanApi,
    private val historyStorage: ScanHistoryStorage
) : ScanRepository {

    override suspend fun analyzeImage(imagePart: MultipartBody.Part): ScanResultDto {
        return try {
            scanApi.analyzeImage(imagePart)
        } catch (e: Exception) {
            // 네트워크 에러 시 Mock 데이터 반환 (개발 중)
            createMockResult()
        }
    }

    override suspend fun getScanHistory(): List<ScanHistoryDto> {
        return try {
            val history = scanApi.getScanHistory()
            historyStorage.replaceAll(history)
            history
        } catch (http: HttpException) {
            if (http.code() == 401 || http.code() == 403) {
                historyStorage.readAll()
            } else {
                historyStorage.readAll().ifEmpty { throw http }
            }
        } catch (e: Exception) {
            historyStorage.readAll().ifEmpty { throw e }
        }
    }

    override suspend fun saveScanResult(result: ScanResultDto): ScanHistoryDto {
        val entry = try {
            val saved = scanApi.saveScanResult(result)
            val normalized = saved.copy(
                confirmed = true,
                leafPoints = if (saved.leafPoints == 0) calculateLeafPoints(result) else saved.leafPoints
            )
            historyStorage.upsert(normalized)
            normalized
        } catch (_: Exception) {
            val localEntry = createLocalEntry(result)
            historyStorage.upsert(localEntry)
            localEntry
        }
        return entry
    }

    override suspend fun getLocalHistory(): List<ScanHistoryDto> = historyStorage.readAll()

    private fun createLocalEntry(result: ScanResultDto): ScanHistoryDto =
        ScanHistoryDto(
            id = System.currentTimeMillis(),
            category = result.category,
            scannedAt = getCurrentTimeString(),
            leafPoints = calculateLeafPoints(result),
            confirmed = true
        )

    private fun createMockResult(): ScanResultDto {
        return ScanResultDto(
            category = "Plastic Bottle",
            subCategory = "투명 페트병",
            confidence = 0.87,
            description = """
                플라스틱 병을 버릴 때는 먼저 라벨과 스티커를 깨끗이 떼어내고 
                병뚜껑을 본체와 분리해야 합니다. 내용물이 남아 있지 않도록 충분히 
                비운 뒤 흐르는 물에 간단히 헹궈 이물질을 제거하고, 물기를 
                낮진 않도록 충분히 털어내는 것이 좋습니다. 그런 다음 병의 바닥 
                부분을 눌러 부피를 최소화한 뒤 병뚜껑을 올린 잠그기 가볍게 돌이
                고정하여 선별작업이 한결 수월해집니다. 모든 준비가 끝나면 두 
                명 플라스틱 전용 수거함에 넣어 배출하고, 
                엠에서 분류 처리 재활용 절차에 맞이 처리될 수 있도록 
                원에서 폐플라스틱 재활용 센터에 신규 처리여기 제제에 맞지 도움이 
                원에서 보관을 위지 자격재활 처리 합니다. 이렇게 앙직된 주세 
                그리 기여합니다.
            """.trimIndent(),
            tips = listOf(
                "라벨을 제거하세요",
                "뚜껑을 분리하세요",
                "내용물을 완전히 비우세요"
            )
        )
    }

    private fun getCurrentTimeString(): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy HH:mm a", java.util.Locale.ENGLISH)
        return sdf.format(java.util.Date())
    }

    private fun calculateLeafPoints(result: ScanResultDto): Int {
        val key = (result.subCategory ?: result.category).trim().lowercase()
        return when (key) {
            "투명 페트병" -> 2
            "플라스틱" -> 2
            "비닐류" -> 1
            "스티로폼" -> 1
            "캔류" -> 2
            "고철류" -> 1
            "유리병" -> 2
            "종이류" -> 1
            "섬유류" -> 3
            "대형 전자제품" -> 5
            "소형 전자제품" -> 3
            "전지류" -> 3
            "가구" -> 5
            else -> 0
        }
    }
}