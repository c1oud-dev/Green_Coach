package com.greencoach.service

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Co2ServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var service: Co2Service

    private fun enqueueCsv(csv: String, code: Int = 200) {
        server.enqueue(
            MockResponse()
                .setResponseCode(code)
                .addHeader("Content-Type", "text/csv; charset=utf-8")
                .setBody(csv.trimIndent())
        )
    }

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
        val baseUrl = server.url("/").toString().removeSuffix("/")
        val webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build()

        // co2CsvPath는 상대 경로 사용 (서비스에서 get().uri(co2CsvPath))
        val co2CsvPath = "/owid-co2-data.csv"
        val yearsLimit = 3

        service = Co2Service(
            owidWebClient = webClient,
            baseUrl = baseUrl,
            co2CsvPath = co2CsvPath,
            yearsLimit = yearsLimit
        )
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    @DisplayName("RAW 스키마(country,iso_code,year,co2,...)에서 World/Korea 파싱")
    fun parseRawSchema_world_korea() {
        val csv = """
            country,iso_code,year,co2
            World,OWID_WRL,2021,6800
            World,OWID_WRL,2022,7000
            Korea,KOR,2021,1500
            Korea,KOR,2022,1600
        """
        enqueueCsv(csv)

        StepVerifier.create(service.getWorld())
            .assertNext { snap ->
                // yearsLimit=3이므로 모두 포함(2개)
                assertEquals("World Emissions", snap.emissions.label)
                assertEquals(2, snap.emissions.points.size)
                assertEquals(2022, snap.emissions.points.last().year)
                assertEquals(7000.0, snap.emissions.points.last().value)
                // reduction = max(prev - curr, 0)
                assertEquals(0.0, snap.reduction.points.last().value)
            }
            .verifyComplete()

        // 두 번째 요청 큐에 없으므로 다시 넣어줌
        enqueueCsv(csv)

        StepVerifier.create(service.getKorea())
            .assertNext { snap ->
                assertEquals("Korea Emissions", snap.emissions.label)
                assertEquals(2, snap.emissions.points.size)
                assertEquals(1600.0, snap.emissions.points.last().value)
                assertEquals(0.0, snap.reduction.points.last().value)
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("Grapher 스키마(Entity,Code,Year,CO2,...)에서도 정상 파싱 (World는 Code 빈칸, Entity=World)")
    fun parseGrapherSchema_world_korea() {
        val csv = """
            Entity,Code,Year,CO2
            World,,2020,6500
            World,,2021,6600
            Korea,KOR,2020,1400
            Korea,KOR,2021,1300
        """
        enqueueCsv(csv)

        StepVerifier.create(service.getWorld())
            .assertNext { snap ->
                assertEquals(2, snap.emissions.points.size)
                assertEquals(6600.0, snap.emissions.points.last().value)
            }
            .verifyComplete()

        enqueueCsv(csv)
        StepVerifier.create(service.getKorea())
            .assertNext { snap ->
                assertEquals(2, snap.emissions.points.size)
                // 2021이 1300으로 감소 → reduction(2021)=max(1400-1300,0)=100
                assertEquals(100.0, snap.reduction.points.last().value)
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("yearsLimit 적용: 최근 N개만 반환")
    fun yearsLimit_applied() {
        val csv = """
            country,iso_code,year,co2
            World,OWID_WRL,2019,6000
            World,OWID_WRL,2020,6100
            World,OWID_WRL,2021,6200
            World,OWID_WRL,2022,6300
        """
        enqueueCsv(csv)

        StepVerifier.create(service.getWorld())
            .assertNext { snap ->
                // yearsLimit = 3 -> 2020,2021,2022
                assertEquals(3, snap.emissions.points.size)
                val years = snap.emissions.points.map { it.year }
                assertEquals(listOf(2020, 2021, 2022), years)
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("헤더가 예상과 다르면 빈 스냅샷 반환")
    fun malformedHeader_returnsEmpty() {
        val csv = """
            A,B,C,D
            x,y,z,w
        """
        enqueueCsv(csv)

        StepVerifier.create(service.getWorld())
            .assertNext { snap ->
                assertTrue(snap.emissions.points.isEmpty())
                assertTrue(snap.reduction.points.isEmpty())
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("빈 CSV면 빈 스냅샷 반환")
    fun emptyCsv_returnsEmpty() {
        enqueueCsv("") // 빈 응답

        StepVerifier.create(service.getKorea())
            .assertNext { snap ->
                assertTrue(snap.emissions.points.isEmpty())
                assertTrue(snap.reduction.points.isEmpty())
            }
            .verifyComplete()
    }
}