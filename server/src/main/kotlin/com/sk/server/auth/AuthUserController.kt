package com.sk.server.auth

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/user")
class AuthUserController(
    private val authUserService: AuthUserService,
) {
    @GetMapping("/me")
    fun getMe(authentication: Authentication): UserResponse {
        return authUserService.createOrGet(authentication.principal as Jwt)
    }
}