package com.skill.job.common.filter

import com.auth0.jwt.exceptions.JWTVerificationException
import com.skill.job.common.config.security.JwtAuthenticationToken
import com.skill.job.common.config.security.JwtUtil
import com.skill.job.member.dto.AuthMember
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtSecurityFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            try {
                val token = jwtUtil.substringToken(header)
                val userId = jwtUtil.extractUserId(token)
                val email = jwtUtil.extractEmail(token)
                val role = jwtUtil.extractMemberRole(token)

                if (SecurityContextHolder.getContext().authentication == null) {
                    val authMember = AuthMember(userId, email, role)
                    val authenticationToken = JwtAuthenticationToken(authMember)
                    authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authenticationToken
                }
            } catch (ex: JWTVerificationException) {
                logger.error("JWT verification failed: ${ex.message}")
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token")
                return
            } catch (ex: Exception) {
                logger.error("JWT processing failed", ex)
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                return
            }
        }
        chain.doFilter(request, response)
    }
}
