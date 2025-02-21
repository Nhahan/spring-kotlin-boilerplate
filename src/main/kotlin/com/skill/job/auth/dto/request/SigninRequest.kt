package com.skill.job.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SigninRequest(
    @field:NotBlank(message = "Email must not be blank")
    @field:Email(message = "Invalid email address")
    val email: String,
    @field:NotBlank(message = "Password must not be blank")
    val password: String
)
