package com.sk.server

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

interface PushNotificationService {
    fun send(deviceToken: String, message: String)
}

@Service
class ApnsService(
    @Value("\${apns.team-id}") private val teamId: String,
    @Value("\${apns.key-id}") private val keyId: String,
    @Value("\${apns.bundle-id}") private val bundleId: String,
    @Value("\${apns.auth-key}") private val authKey: String,
): PushNotificationService {
    private val httpClient = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .build()

    override fun send(deviceToken: String, message: String) {
        val jwtToken = generateJwtToken()
        val payload = """{"aps": {"alert": "$message", "sound": "default"}}"""
        val url = "https://api.sandbox.push.apple.com:443/3/device/$deviceToken"
        val request = Request.Builder()
            .url(url)
            .post(payload.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .addHeader("Authorization", "Bearer $jwtToken")
            .addHeader("apns-topic", bundleId)
            .build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
        }
    }

    private fun generateJwtToken(): String {
        val nowMillis = System.currentTimeMillis()
        val now = Date(nowMillis)
        val keyBytes = Base64.getDecoder().decode(authKey)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("EC")
        val key = keyFactory.generatePrivate(keySpec)

        return Jwts.builder()
            .setHeaderParam("kid", keyId)
            .setIssuer(teamId)
            .setIssuedAt(now)
            .setExpiration(Date(nowMillis + 120 * 1000))
            .signWith(key, SignatureAlgorithm.ES256)
            .compact()
    }
}