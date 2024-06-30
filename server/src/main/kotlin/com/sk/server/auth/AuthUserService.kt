package com.sk.server.auth

import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit


data class UserResponse(
    val email: String,
    val accessToken: String,
)

interface AuthUserService {
    fun createOrGet(user: Jwt): UserResponse
}

@Service
class DefaultAuthUserService(
    private val jwtEncoder: JwtEncoder
): AuthUserService {
    override fun createOrGet(user: Jwt): UserResponse {
        val email = user.getClaimAsString("email")
        val jwsHeader = JwsHeader.with { "HS256" }.build()
        val claims = JwtClaimsSet.builder()
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(30L, ChronoUnit.DAYS))
            .claim("email", email)
            .build()
        val accessToken = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).tokenValue
        return UserResponse(email, accessToken)
    }
}