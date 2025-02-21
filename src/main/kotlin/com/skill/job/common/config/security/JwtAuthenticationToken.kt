package com.skill.job.common.config.security

import com.skill.job.member.dto.AuthMember
import org.springframework.security.authentication.AbstractAuthenticationToken

class JwtAuthenticationToken(
    private val authMember: AuthMember
) : AbstractAuthenticationToken(authMember.authorities) {
    init {
        super.setAuthenticated(true)
    }

    override fun getCredentials(): Any? = null

    override fun getPrincipal(): Any = authMember
}
