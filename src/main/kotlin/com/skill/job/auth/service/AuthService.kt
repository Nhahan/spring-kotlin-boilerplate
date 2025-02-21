package com.skill.job.auth.service

import com.skill.job.auth.dto.request.SigninRequest
import com.skill.job.auth.dto.request.SignupRequest
import com.skill.job.auth.dto.response.AuthResponse
import com.skill.job.common.config.security.JwtUtil
import com.skill.job.common.exception.AppException
import com.skill.job.member.dto.AuthMember
import com.skill.job.member.entity.Member
import com.skill.job.member.repository.MemberRepository
import org.example.statelessspringsecurity.enums.MemberRole
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtUtil: JwtUtil,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun signup(request: SignupRequest): AuthResponse {
        if (memberRepository.findByEmail(request.email) != null) {
            throw AppException(HttpStatus.CONFLICT.value(), "이미 존재하는 이메일입니다.")
        }

        val member =
            Member.create(
                email = request.email,
                rawPassword = request.password,
                phone = request.phone,
                name = request.name,
                birthDate = request.birthDate,
                passwordEncoder = passwordEncoder
            )
        val savedMember = memberRepository.save(member)

        val role =
            try {
                MemberRole.of(request.role)
            } catch (e: Exception) {
                throw AppException(HttpStatus.BAD_REQUEST.value(), "잘못된 회원 역할입니다: ${request.role}")
            }

        // 도메인 객체를 기반으로 인증용 객체를 생성
        val authMember = AuthMember(savedMember.id, savedMember.email, role)
        val token = jwtUtil.createToken(authMember.userId, authMember.email, role)
        return AuthResponse(token)
    }

    fun signin(request: SigninRequest): AuthResponse {
        val member =
            memberRepository.findByEmail(request.email)
                ?: throw AppException(HttpStatus.UNAUTHORIZED.value(), "사용자를 찾을 수 없습니다.")
        if (!member.checkPassword(request.password, passwordEncoder)) {
            throw AppException(HttpStatus.UNAUTHORIZED.value(), "비밀번호가 올바르지 않습니다.")
        }

        val role = MemberRole.MEMBER
        val authMember = AuthMember(member.id, member.email, role)
        val token = jwtUtil.createToken(authMember.userId, authMember.email, role)
        return AuthResponse(token)
    }
}
