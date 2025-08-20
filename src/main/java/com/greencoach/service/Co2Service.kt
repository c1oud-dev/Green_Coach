package com.greencoach.service

import com.greencoach.model.Co2PointDto
import com.greencoach.model.Co2SeriesDto
import com.greencoach.model.Co2SnapshotDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import kotlin.math.max

@Service
class Co2Service (
    private val owidWebClient: WebClient,
    @Value("\${co2.base-url}") private val baseUrl: String,
    @Value("\${co2.co2-csv-path}") private val co2CsvPath: String,
    @Value("\${co2.years-limit}") private val yearsLimit: Int,
) {


    /**
     * GitHub CSV 헤더: country,iso_code,year,co2, ...
     * World,OWID_WRL,2019,xxxxx
     * Korea, KOR, 2019, xxxxx
     */
    fun getWorld(): Mono<Co2SnapshotDto> = fetchSnapshotByCode("OWID_WRL")
    fun getKorea(): Mono<Co2SnapshotDto> = fetchSnapshotByCode("KOR")

    private fun fetchSnapshotByCode(code: String): Mono<Co2SnapshotDto> {
        // owidWebClient는 baseUrl이 이미 설정되어 있음
        return owidWebClient.get()
            .uri(co2CsvPath) // 상대경로 사용
            .retrieve()
            .bodyToMono(String::class.java)
            .map { csv -> parseCsvAndBuildByCodeFlexible(code, csv) }
    }

    private fun parseCsvAndBuildByCodeFlexible(targetCode: String, csv: String): Co2SnapshotDto {
        val lines = csv.lineSequence().filter { it.isNotBlank() }.toList()
        if (lines.isEmpty()) return emptySnapshot(targetCode)

        val header = lines.first().split(',').map { it.trim().lowercase() }
        fun idx(name1: String, name2: String? = null): Int {
            val i1 = header.indexOf(name1)
            if (i1 >= 0) return i1
            if (name2 != null) {
                val i2 = header.indexOf(name2)
                if (i2 >= 0) return i2
            }
            return -1
        }

        // GitHub RAW: country,iso_code,year,co2,...
        // Grapher    : Entity,Code,Year,CO2,...
        val idxEntity = idx("country", "entity")
        val idxCode   = idx("iso_code", "code")
        val idxYear   = idx("year")
        val idxCo2    = idx("co2", "co2 (kt)")

        if (idxYear < 0 || idxCo2 < 0 || (idxCode < 0 && idxEntity < 0)) {
            return emptySnapshot(targetCode)
        }

        val pairs = lines.drop(1).mapNotNull { line ->
            val cols = line.split(',')
            if (cols.size <= maxOf(idxYear, idxCo2, idxCode.coerceAtLeast(0), idxEntity.coerceAtLeast(0))) return@mapNotNull null
            val code = if (idxCode >= 0) cols[idxCode].trim() else ""
            val entity = if (idxEntity >= 0) cols[idxEntity].trim() else ""
            val year = cols[idxYear].toIntOrNull()
            val co2  = cols[idxCo2].toDoubleOrNull()

            // targetCode 매칭: 우선 코드, 없을 때 World 대응(엔티티명)
            val match = if (targetCode == "OWID_WRL") {
                // RAW에서는 code=OWID_WRL, Grapher에서는 entity="World" & code=""
                code == "OWID_WRL" || entity.equals("World", ignoreCase = true)
            } else {
                code == targetCode
            }

            if (match && year != null && co2 != null) year to co2 else null
        }.sortedBy { it.first }

        val last = if (pairs.size > yearsLimit) pairs.takeLast(yearsLimit) else pairs
        val emissions = last.map { (y, v) -> Co2PointDto(y, v) }
        val reductions = last.mapIndexed { i, (y, curr) ->
            val prev = if (i == 0) curr else last[i - 1].second
            Co2PointDto(y, kotlin.math.max(prev - curr, 0.0))
        }

        val label = when (targetCode) { "OWID_WRL" -> "World"; "KOR" -> "Korea"; else -> targetCode }
        return Co2SnapshotDto(
            emissions = Co2SeriesDto("$label Emissions", emissions),
            reduction = Co2SeriesDto("$label Reduction", reductions)
        )
    }

    private fun emptySnapshot(targetCode: String) = Co2SnapshotDto(
        emissions = Co2SeriesDto("${targetCode} Emissions", emptyList()),
        reduction = Co2SeriesDto("${targetCode} Reduction", emptyList())
    )
}