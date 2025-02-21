package com.skill.job.auth.controller

import com.skill.job.auth.dto.request.SigninRequest
import com.skill.job.auth.dto.request.SignupRequest
import com.skill.job.auth.dto.response.AuthResponse
import com.skill.job.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/auth/signin")
    fun signin(
        @Valid @RequestBody request: SigninRequest
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.signin(request))
    }

    @PostMapping("/auth/signup")
    fun signup(
        @Valid @RequestBody request: SignupRequest
    ): ResponseEntity<AuthResponse> {
        val response = authService.signup(request)
        return ResponseEntity.ok(response)
    }
}
