package com.skill.job.member.entity

import com.skill.job.common.entity.BaseEntity
import jakarta.persistence.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate

@Entity
@Table(name = "members")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true)
    val email: String,
    val password: String,
    val phone: String? = null,
    val name: String,
    val birthDate: LocalDate
) : BaseEntity() {
    companion object {
        fun create(
            email: String,
            rawPassword: String,
            phone: String?,
            name: String,
            birthDate: LocalDate,
            passwordEncoder: PasswordEncoder
        ): Member {
            return Member(
                email = email,
                password = passwordEncoder.encode(rawPassword),
                phone = phone,
                name = name,
                birthDate = birthDate
            )
        }
    }

    fun checkPassword(
        rawPassword: String,
        passwordEncoder: PasswordEncoder
    ): Boolean {
        return passwordEncoder.matches(rawPassword, password)
    }
}
