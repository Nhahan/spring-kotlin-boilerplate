package com.skill.job.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.skill.job.auth.dto.request.SigninRequest
import com.skill.job.auth.dto.request.SignupRequest
import com.skill.job.auth.dto.response.AuthResponse
import com.skill.job.auth.service.AuthService
import org.assertj.core.api.Assertions.assertThat
import org.example.statelessspringsecurity.enums.MemberRole
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate

@WebMvcTest(controllers = [AuthController::class], excludeAutoConfiguration = [SecurityAutoConfiguration::class])
@ContextConfiguration(classes = [AuthController::class])
class AuthControllerTest(
    wac: WebApplicationContext
) {

    @MockitoBean
    lateinit var authService: AuthService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private val mockMvcTester: MockMvcTester = MockMvcTester.from(wac)

    @Test
    fun `회원가입 API - 신규 회원 등록 시 JWT 토큰 반환`() {
        val signupRequest = SignupRequest(
            email = "newuser@example.com",
            password = "password",
            phone = "01012345678",
            firstName = "New",
            lastName = "User",
            birthDate = LocalDate.of(1995, 5, 5),
            role = MemberRole.MEMBER.name
        )
        val dummyToken = "tokenValue"
        whenever(authService.signup(any())).thenReturn(AuthResponse(dummyToken))

        val mvcResult = mockMvcTester.post().uri("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signupRequest))
            .exchange()

        assertThat(mvcResult)
            .hasStatusOk()
            .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .bodyJson()
            .extractingPath("$.token")
            .isEqualTo(dummyToken)
    }

    @Test
    fun `로그인 API - 올바른 자격 증명 시 JWT 토큰 반환`() {
        val signinRequest = SigninRequest(
            email = "user@example.com",
            password = "password"
        )
        val dummyToken = "tokenValue"
        whenever(authService.signin(any())).thenReturn(AuthResponse(dummyToken))

        val mvcResult = mockMvcTester.post().uri("/auth/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signinRequest))
            .exchange()

        assertThat(mvcResult)
            .hasStatusOk()
            .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .bodyJson()
            .extractingPath("$.token")
            .isEqualTo(dummyToken)
    }
}
