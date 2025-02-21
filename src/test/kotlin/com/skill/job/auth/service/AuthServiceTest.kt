package com.skill.job.auth.service

import com.skill.job.auth.dto.request.SigninRequest
import com.skill.job.auth.dto.request.SignupRequest
import com.skill.job.auth.dto.response.AuthResponse
import com.skill.job.common.config.security.JwtUtil
import com.skill.job.common.exception.AppException
import com.skill.job.member.entity.Member
import com.skill.job.member.repository.MemberRepository
import org.example.statelessspringsecurity.enums.MemberRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

    @Mock
    lateinit var jwtUtil: JwtUtil

    @Mock
    lateinit var memberRepository: MemberRepository

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    lateinit var authService: AuthService

    @Test
    fun `회원가입 성공 시 토큰 발급`() {
        // given
        val request = SignupRequest(
            email = "new@example.com",
            password = "password",
            phone = "01012345678",
            firstName = "New",
            lastName = "User",
            birthDate = LocalDate.of(1995, 5, 5),
            role = MemberRole.MEMBER.name
        )
        whenever(memberRepository.findByEmail(request.email)).thenReturn(null)
        val encodedPassword = "encodedPassword"
        whenever(passwordEncoder.encode(request.password)).thenReturn(encodedPassword)
        // Member.create(...)를 호출하면 생성된 Member를 리턴하도록 함
        val member = Member.create(
            email = request.email,
            rawPassword = request.password,
            phone = request.phone,
            firstName = request.firstName,
            lastName = request.lastName,
            birthDate = request.birthDate,
            passwordEncoder = passwordEncoder
        )
        // 저장 후 id가 할당된 Member 객체 생성
        val savedMember = Member(
            id = 1L,
            email = member.email,
            password = member.password,
            phone = member.phone,
            firstName = member.firstName,
            lastName = member.lastName,
            birthDate = member.birthDate
        )
        whenever(memberRepository.save(any(Member::class.java))).thenReturn(savedMember)
        val dummyToken = "dummy.jwt.token"
        whenever(jwtUtil.createToken(savedMember.id, savedMember.email, MemberRole.MEMBER))
            .thenReturn(dummyToken)

        // when
        val response: AuthResponse = authService.signup(request)

        // then
        assertEquals(dummyToken, response.token)
        verify(memberRepository).findByEmail(request.email)
        verify(memberRepository).save(any(Member::class.java))
        verify(jwtUtil).createToken(savedMember.id, savedMember.email, MemberRole.MEMBER)
    }

    @Test
    fun `이미 존재하는 이메일일 경우 회원가입 실패`() {
        // given
        val request = SignupRequest(
            email = "exist@example.com",
            password = "password",
            phone = null,
            firstName = "Exist",
            lastName = "User",
            birthDate = LocalDate.of(1990, 1, 1),
            role = "USER"
        )
        whenever(memberRepository.findByEmail(request.email)).thenReturn(
            Member(
                id = 1L,
                email = request.email,
                password = "encoded",
                phone = null,
                firstName = "Exist",
                lastName = "User",
                birthDate = LocalDate.of(1990, 1, 1)
            )
        )

        // when/then
        val exception = assertThrows<AppException> { authService.signup(request) }
        assertEquals(HttpStatus.CONFLICT.value(), exception.status)
    }

    @Test
    fun `로그인 성공 시 토큰 발급`() {
        // given
        val request = SigninRequest(
            email = "user@example.com",
            password = "password"
        )
        val member = Member(
            id = 1L,
            email = request.email,
            password = "encodedPassword",
            phone = null,
            firstName = "Test",
            lastName = "User",
            birthDate = LocalDate.of(1990, 1, 1)
        )
        whenever(memberRepository.findByEmail(request.email)).thenReturn(member)
        whenever(passwordEncoder.matches(request.password, member.password)).thenReturn(true)
        val dummyToken = "dummy.jwt.token"
        whenever(jwtUtil.createToken(member.id, member.email, MemberRole.MEMBER))
            .thenReturn(dummyToken)

        // when
        val response = authService.signin(request)

        // then
        assertEquals(dummyToken, response.token)
        verify(memberRepository).findByEmail(request.email)
        verify(passwordEncoder).matches(request.password, member.password)
        verify(jwtUtil).createToken(member.id, member.email, MemberRole.MEMBER)
    }

    @Test
    fun `존재하지 않는 사용자의 경우 로그인 실패`() {
        // given
        val request = SigninRequest(
            email = "nonexistent@example.com",
            password = "password"
        )
        whenever(memberRepository.findByEmail(request.email)).thenReturn(null)

        // when/then
        val exception = assertThrows<AppException> { authService.signin(request) }
        assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.status)
    }

    @Test
    fun `비밀번호 불일치 시 로그인 실패`() {
        // given
        val request = SigninRequest(
            email = "user@example.com",
            password = "wrongPassword"
        )
        val member = Member(
            id = 1L,
            email = request.email,
            password = "encodedPassword",
            phone = null,
            firstName = "Test",
            lastName = "User",
            birthDate = LocalDate.of(1990, 1, 1)
        )
        whenever(memberRepository.findByEmail(request.email)).thenReturn(member)
        whenever(passwordEncoder.matches(request.password, member.password)).thenReturn(false)

        // when/then
        val exception = assertThrows<AppException> { authService.signin(request) }
        assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.status)
    }
}
