package com.greencoach.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret:change-me-please-32bytes-secret-change-me}")
    private val secret: String,
    @Value("\${jwt.expiresMillis:86400000}") // 1 day
    private val expiresMillis: Long
) {
    private val key by lazy { Keys.hmacShaKeyFor(secret.toByteArray()) }

    fun createToken(subject: String): String {
        val now = Date()
        val exp = Date(now.time + expiresMillis)
        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getSubject(token: String): String =
        Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).body.subject
}
