package com.application.frontend.ui.screen

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import com.application.frontend.MainActivity
import com.application.frontend.di.NetworkModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * 프로필 탭 중심으로 다음을 자동 검증:
 * 1) 로그인 성공 → ProfileHomeScreen 도달
 * 2) 비밀번호 재설정: Forgot → Verify → Reset → PasswordChanged → Back to login
 * 3) 회원가입 화면: 진입 → 필드 입력 → "Log in" 눌러 로그인 화면 복귀
 *
 * 주의: NetworkModule은 androidTest의 TestNetworkModule로 대체됩니다.
 */

private fun not(m: SemanticsMatcher): SemanticsMatcher =
    SemanticsMatcher("NOT(${m.description})") { node -> !m.matches(node) }


@HiltAndroidTest
@UninstallModules(NetworkModule::class)
class ProfileFlowsTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createEmptyComposeRule()

    @Inject lateinit var server: MockWebServer
    lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        // 1) Hilt 먼저 준비
        hiltRule.inject()

        // 2) 서버 시작(백그라운드 쓰레드에서 처리되므로 메인 스레드 네트워크 예외 없음)
        server.start(0) // 임의 포트
        // 3) DI가 읽을 baseUrl 주입
        com.application.frontend.di.TestNetworkModule.TestServerConfig.baseUrl =
            server.url("/").toString().replace("localhost", "127.0.0.1")


        // 4) 디스패처
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(req: RecordedRequest): MockResponse = when {
                req.path?.startsWith("/auth/login")  == true ->
                    MockResponse().setResponseCode(200)
                        .setBody("""{"token":"fake.jwt.token"}""")
                        .addHeader("Content-Type","application/json")

                req.path?.startsWith("/auth/signup") == true ->
                    MockResponse().setResponseCode(200)
                        .setBody("""{"token":"fake.jwt.token"}""")
                        .addHeader("Content-Type","application/json")

                // 홈 초기 로딩: 뉴스 호출은 빈 배열로
                req.path?.startsWith("/api/news") == true ->
                    MockResponse().setResponseCode(200)
                        .setBody("[]").addHeader("Content-Type","application/json")

                else -> MockResponse().setResponseCode(404)
            }
        }

        // 5) 이제 액티비티 실행
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }


    @After
    fun tearDown() {
        if (this::scenario.isInitialized) scenario.close()
        server.shutdown()
    }


    /** 1) 로그인 성공 → ProfileHomeScreen 도달 */
    @Test
    fun login_success_navigates_to_profileHome() {
        composeRule.onNodeWithText("Profile").performClick()

        // 이메일 입력창
        composeRule.onNodeWithTag("login_email").performTextInput("green@example.com")

        // 비밀번호 입력창
        composeRule.onNodeWithTag("login_password").performTextInput("password1234")

        // 로그인 버튼
        composeRule.onNodeWithTag("login_button").performClick()

        // 도달 확인은 타이틀 태그로 단일 매칭
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodes(hasText("Profile")).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("profile_title", useUnmergedTree = true).assertIsDisplayed()
    }

    /** 2) 비밀번호 재설정 전체 플로우 */
    @Test
    fun forgot_verify_reset_passwordChanged_back_to_login() {
        composeRule.onNodeWithText("Profile").performClick()

        // Forgot → 이메일 입력 화면
        composeRule.onNodeWithText("Forgot password?").performClick()
        composeRule.onNodeWithText("Email address").assertIsDisplayed()
        composeRule.onNodeWithText("Send code").performClick()

        // Verify (4자리 코드)
        composeRule.onNodeWithText("Please check your email").assertIsDisplayed()
        composeRule.onNodeWithText("Enter 4-digit code").performTextInput("1234")
        composeRule.onNodeWithText("Verify").performClick()

        // Reset
        // 제목 확인 - 더 구체적인 선택자 사용
        composeRule.onAllNodes(hasText("Reset password"))
            .filterToOne(not(hasClickAction()))
            .assertIsDisplayed()

        composeRule.onNodeWithText("must be 8 characters").performTextInput("newPass5678")
        composeRule.onNodeWithText("repeat password").performTextInput("newPass5678")

        // 버튼 클릭 (클릭 가능한 것만 선택)
        composeRule.onAllNodes(hasText("Reset password"))
            .filterToOne(hasClickAction())
            .performClick()

        // PasswordChanged → Back to login
        composeRule.onNodeWithText("Password changed").assertIsDisplayed()
        composeRule.onNodeWithText("Back to login").performClick()

        // 로그인 화면(제목) 복귀 확인
        composeRule.onNodeWithText("로그인 후 이용해주세요").assertIsDisplayed()
    }

    /**
     * 3) 회원가입 화면: 진입/입력/로그인 화면 복귀
     * (현재 SignUpScreen은 onSignUp 콜백이 MainScreen에서 내비게이션으로 연결되어 있지 않으므로
     *  성공 네비게이션 대신 "Log in" 링크로 복귀 동작만 검증합니다.)
     */
    @Test
    fun signup_success_navigates_to_profileHome() {
        // 1) 하단 탭 → Profile(로그인 전 화면)
        composeRule.onNodeWithText("Profile").performClick()

        // 2) Sign up 링크로 회원가입 화면 진입
        composeRule.onNodeWithText("Sign up").performClick()
        composeRule.onNodeWithText("Create Account").assertIsDisplayed() // 타이틀 확인

        // 3) TextField 3개(닉네임/이메일/비밀번호) 순서대로 채우기
        composeRule.onNodeWithTag("signup_nickname").performTextInput("green")
        composeRule.onNodeWithTag("signup_email").performTextInput("green@example.com")
        composeRule.onNodeWithTag("signup_password").performTextInput("password1234")
        composeRule.onNodeWithTag("signup_button").performClick()

        composeRule.onNodeWithTag("profile_title", useUnmergedTree = true).assertIsDisplayed()
    }

    private fun ComposeTestRule.waitUntil(
        timeoutMillis: Long = 1000,
        condition: () -> Boolean
    ) {
        this.waitUntil(timeoutMillis) { condition() }
    }

    /*// 수동 데모 전용 - 기본 테스트 실행에 섞이지 않게 @Ignore 권장
    @org.junit.Ignore("Manual demo only — run this test alone to explore the UI")
    @Test
    fun manual_demo_session() {
        // 여기까지 오면 setUp()이 이미 server.start + dispatcher + MainActivity.launch 해둔 상태
        // 이제 3분 동안 직접 에뮬레이터에서 화면 조작 가능
        println("🔎 Manual demo: You can interact with the app now (3 minutes)…")
        Thread.sleep(50000)
    }*/

}

// ===== Manual Demo (수동 체험 전용; 원하는 테스트만 단독 실행) =====
// ⏱️ 전역 속도 설정(원하면 숫자만 바꿔서 더 느리게/빠르게)
private const val TYPE_DELAY_MS = 35000L    // 글자당 입력 간격
private const val CLICK_PRE_MS = 70000L     // 클릭 전 대기
private const val CLICK_POST_MS = 70000L    // 클릭 후 대기

private fun sleep(ms: Long) = Thread.sleep(ms)

// 글자 하나씩 천천히 입력
private fun SemanticsNodeInteraction.slowType(text: String, stepMs: Long = TYPE_DELAY_MS) {
    for (ch in text) {
        this.performTextInput(ch.toString())
        sleep(stepMs)
    }
}

// 클릭도 여유 두고 실행
private fun SemanticsNodeInteraction.slowClick(
    pauseBefore: Long = CLICK_PRE_MS,
    pauseAfter: Long = CLICK_POST_MS
) {
    sleep(pauseBefore)
    // 스크롤 가능한 경우를 대비해(없어도 무해)
    try { this.performScrollTo() } catch (_: Throwable) {}
    this.performClick()
    sleep(pauseAfter)
}

// 화면 멈춤(수동 조작 시간 확보)
private fun freeze(label: String, seconds: Int = 18000) {
    println("⏸️ $label — pausing for $seconds s")
    Thread.sleep(seconds * 1000L)
}


@HiltAndroidTest
@UninstallModules(NetworkModule::class)
class ProfileManualDemoTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createEmptyComposeRule()

    @Inject lateinit var server: MockWebServer
    lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        hiltRule.inject()

        // 서버 시작 + baseUrl 주입 (localhost -> 127.0.0.1 치환으로 보안설정 매칭 확실하게)
        server.start(0)
        com.application.frontend.di.TestNetworkModule.TestServerConfig.baseUrl =
            server.url("/").toString().replace("localhost", "127.0.0.1")

        // 느리게 보여주고 싶을 때 네트워크 지연 넣기 (로딩/전환 감상용)
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(req: RecordedRequest): MockResponse = when {
                req.path?.startsWith("/auth/login") == true ->
                    MockResponse().setResponseCode(200)
                        .setBody("""{"token":"fake.jwt.token"}""")
                        .addHeader("Content-Type","application/json")
                        .setBodyDelay(50_000, TimeUnit.MILLISECONDS)

                req.path?.startsWith("/auth/signup") == true ->
                    MockResponse().setResponseCode(200)
                        .setBody("""{"token":"fake.jwt.token"}""")
                        .addHeader("Content-Type","application/json")
                        .setBodyDelay(50_000, TimeUnit.MILLISECONDS)

                req.path?.startsWith("/api/news") == true ->
                    MockResponse().setResponseCode(200)
                        .setBody("[]").addHeader("Content-Type","application/json")
                        .setBodyDelay(5_000, TimeUnit.MILLISECONDS)

                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    @After
    fun tearDown() {
        if (this::scenario.isInitialized) scenario.close()
        server.shutdown()
    }

    private fun freeze(label: String, seconds: Int = 180) {
        println("⏸️ $label — pausing for $seconds s")
        Thread.sleep(seconds * 1000L)
    }

    @org.junit.Ignore("Manual demo only — run alone")
    @Test fun demo_home_pause() {
        // setUp에서는 액티비티를 안 띄움! → 여기서 원하는 순간에 띄우기
        scenario = ActivityScenario.launch(MainActivity::class.java)
        // 홈에서 2~3분 정지
        freeze("Home", 120)
    }

    @org.junit.Ignore("Manual demo only — run alone")
    @Test fun demo_login_pause() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeRule.onNodeWithText("Profile").performClick() // 하단 탭 이동
        composeRule.onNodeWithTag("login_email").assertExists()
        // 로그인 화면에서 정지
        freeze("Login", 180)
    }

    @org.junit.Ignore("Manual demo only — run alone")
    @Test fun demo_signup_pause() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        composeRule.onNodeWithText("Profile").slowClick()
        composeRule.onNodeWithText("Sign up").slowClick()

        composeRule.onNodeWithTag("signup_nickname").slowType("green")
        composeRule.onNodeWithTag("signup_email").slowType("green@example.com")
        composeRule.onNodeWithTag("signup_password").slowType("password1234")

        // 제출 직전에 멈춰서 화면을 천천히 확인
        freeze("Sign Up (before submit)", 180)
        composeRule.onNodeWithTag("signup_button").slowClick()
        freeze("After Sign Up", 120)
    }


    @org.junit.Ignore("Manual demo only — run alone")
    @Test fun demo_profile_after_login_pause() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // 하단 탭 'Profile'까지도 느리게 이동
        composeRule.onNodeWithText("Profile").slowClick()

        // 천천히 타이핑
        composeRule.onNodeWithTag("login_email").slowType("green@example.com")
        composeRule.onNodeWithTag("login_password").slowType("password1234")

        // 로그인 버튼도 느리게 클릭
        composeRule.onNodeWithTag("login_button").slowClick()

        // 프로필 타이틀 뜨면 멈춰서 감상/조작
        composeRule.onNodeWithTag("profile_title", useUnmergedTree = true).assertExists()
        freeze("Profile Home", 180) // 3분 멈춤(원하면 바꿔도 OK)
    }

}
