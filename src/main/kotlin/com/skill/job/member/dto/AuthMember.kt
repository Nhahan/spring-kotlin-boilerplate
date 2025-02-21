package com.skill.job.member.dto

import org.example.statelessspringsecurity.enums.MemberRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class AuthMember(
    val userId: Long,
    val email: String,
    role: MemberRole
) {
    val authorities: Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority(role.name))
}
