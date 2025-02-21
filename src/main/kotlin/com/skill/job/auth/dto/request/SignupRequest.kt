package com.skill.job.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class SignupRequest(
    @field:NotBlank(message = "Email must not be blank")
    @field:Email(message = "Invalid email address")
    val email: String,
    @field:NotBlank(message = "Password must not be blank")
    @field:Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    val password: String,
    val phone: String? = null,
    @field:NotBlank(message = "Name must not be blank")
    val name: String,
    val birthDate: LocalDate,
    @field:NotBlank(message = "Role must not be blank")
    val role: String
)
