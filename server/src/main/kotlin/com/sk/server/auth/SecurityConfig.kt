package com.sk.server.auth

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun authSecurityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager,
    ): SecurityFilterChain {
        return http.securityMatcher("/auth/user/me")
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/auth/user/me")
                it.anyRequest().authenticated()
            }
            .addFilterBefore(BearerTokenAuthenticationFilter(authenticationManager), AnonymousAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun appSecurityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager,
    ): SecurityFilterChain {
        return http.securityMatcher("/api/**")
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .addFilterBefore(BearerTokenAuthenticationFilter(authenticationManager), AnonymousAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun authenticationManager(
        appleJwtDecoder: JwtDecoder,
        toyotagJwtDecoder: JwtDecoder,
    ): AuthenticationManager {
        val appleJwtAuthenticationProvider = JwtAuthenticationProvider(appleJwtDecoder)
        val toyotagJwtAuthenticationProvider = JwtAuthenticationProvider(toyotagJwtDecoder)
        return ProviderManager(appleJwtAuthenticationProvider, toyotagJwtAuthenticationProvider)
    }
}

@Configuration
class JwtCoderConfig(
    @Value("\${auth.secret}") private val secret: String,
) {
    private val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")

    @Bean
    fun appleJwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri("https://appleid.apple.com/auth/keys").build()
    }

    @Bean
    fun toyotagJwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withSecretKey(secretKey).build()
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val secret = ImmutableSecret<SecurityContext>(secretKey)
        return NimbusJwtEncoder(secret)
    }
}