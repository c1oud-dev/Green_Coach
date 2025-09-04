package com.greencoach.integration

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.greencoach.repository.PasswordResetCodeRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.charset.StandardCharsets

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = true) // SecurityFilterChain + JWT 필터까지 실제로 태움
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val resetRepo: PasswordResetCodeRepository
) {
    private val mapper = jacksonObjectMapper()
    private val defaultEmail = "green@example.com"
    private val defaultPassword = "password1234"

    private fun content(result: String) = result.toByteArray(StandardCharsets.UTF_8)
    private fun tokenFrom(body: String): String {
        val json: JsonNode = mapper.readTree(body)
        return json.get("token").asText()
    }

    @Test
    @Order(1)
    fun `회원가입 - 로그인 - me 조회`() {
        // 1) SignUp
        val signUpJson = """
            {
              "nickname": "green",
              "email": "$defaultEmail",
              "password": "$defaultPassword",
              "birth": "2000-01-01",
              "gender": "Male"
            }
        """.trimIndent()

        val signUpRes = mockMvc.perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andReturn()

        val token1 = tokenFrom(signUpRes.response.contentAsString)
        assertNotNull(token1)

        // 2) Login
        val loginJson = """
            {
              "email": "$defaultEmail",
              "password": "$defaultPassword"
            }
        """.trimIndent()

        val loginRes = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andReturn()

        val token2 = tokenFrom(loginRes.response.contentAsString)
        assertNotNull(token2)

        // 3) /users/me
        mockMvc.perform(
            get("/users/me")
                .header("Authorization", "Bearer $token2")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(defaultEmail))
            .andExpect(jsonPath("$.nickname").value("green"))
            .andExpect(jsonPath("$.verified").value(true))
    }

    @Test
    @Order(2)
    fun `프로필 수정(PATCH users_me) - 닉네임, gender`() {
        // 로그인해서 토큰 획득
        val loginJson = """
            {
              "email": "$defaultEmail",
              "password": "$defaultPassword"
            }
        """.trimIndent()

        val loginRes = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
        )
            .andExpect(status().isOk)
            .andReturn()

        val token = tokenFrom(loginRes.response.contentAsString)

        // PATCH /users/me
        val patchJson = """
            {
              "nickname": "greencoach",
              "gender": "Other"
            }
        """.trimIndent()

        val patched = mockMvc.perform(
            patch("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $token")
                .content(patchJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nickname").value("greencoach"))
            .andExpect(jsonPath("$.gender").value("Other"))
            .andReturn()

        // 응답 재확인
        val json = mapper.readTree(patched.response.contentAsString)
        assertEquals(defaultEmail, json.get("email").asText())
    }

    @Test
    @Order(3)
    fun `비밀번호 재설정 플로우 - forgot - verify - reset - 새 비번 로그인`() {
        // 1) forgot
        val forgotJson = """{ "email": "$defaultEmail" }"""
        mockMvc.perform(
            post("/auth/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(forgotJson)
        )
            .andExpect(status().isOk)

        // 2) 서버에 저장된 가장 최신 코드 조회
        val entityOpt = resetRepo.findTopByEmailOrderByIdDesc(defaultEmail)
        val entity = entityOpt.orElseThrow { IllegalStateException("Reset code not saved") }
        val code = entity.code
        assertEquals(4, code.length)

        // 3) verify
        val verifyJson = """
            {
              "email": "$defaultEmail",
              "code": "$code"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(verifyJson)
        )
            .andExpect(status().isOk)

        // 4) reset
        val newPassword = "newPass5678"
        val resetJson = """
            {
              "email": "$defaultEmail",
              "newPassword": "$newPassword"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/auth/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(resetJson)
        )
            .andExpect(status().isOk)

        // 5) 새 비번으로 로그인
        val loginJson = """
            {
              "email": "$defaultEmail",
              "password": "$newPassword"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
    }

    @Test
    @Order(4)
    fun `에러 케이스 - 잘못된 비번 로그인, 중복 회원가입`() {
        // 잘못된 비밀번호
        val badLoginJson = """
            { "email": "$defaultEmail", "password": "wrong-pass" }
        """.trimIndent()

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badLoginJson)
        )
            .andExpect(status().is4xxClientError)

        // 이미 가입된 이메일로 회원가입
        val dupSignUp = """
            {
              "nickname": "dup",
              "email": "$defaultEmail",
              "password": "anypassword"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dupSignUp)
        )
            .andExpect(status().is4xxClientError)
    }
}
