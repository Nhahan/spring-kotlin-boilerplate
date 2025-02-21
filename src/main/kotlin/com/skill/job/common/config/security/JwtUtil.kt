package com.skill.job.common.config.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.example.statelessspringsecurity.enums.MemberRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
) {
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    fun createToken(
        userId: Long,
        email: String,
        role: MemberRole
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)
        return JWT.create()
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withClaim("memberRole", role.name)
            .withIssuedAt(now)
            .withExpiresAt(expiryDate)
            .sign(algorithm)
    }

    fun verifyToken(token: String) = JWT.require(algorithm).build().verify(token)

    fun extractUserId(token: String): Long = verifyToken(token).subject.toLong()

    fun extractEmail(token: String): String = verifyToken(token).getClaim("email").asString()

    fun extractMemberRole(token: String): MemberRole = MemberRole.of(verifyToken(token).getClaim("memberRole").asString())

    fun substringToken(token: String): String {
        val bearerPrefix = "Bearer "
        if (token.startsWith(bearerPrefix)) {
            return token.substring(bearerPrefix.length)
        } else {
            throw IllegalArgumentException("Invalid token format")
        }
    }
}
