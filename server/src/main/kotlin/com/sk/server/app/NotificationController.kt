package com.sk.server.app

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val pushNotificationService: PushNotificationService
) {
    @PostMapping("/send")
    fun sendPushNotification(@RequestBody request: NotificationRequest) {
        pushNotificationService.send(request.deviceToken, request.message)
    }

    @PostMapping("/device-token")
    fun saveDeviceToken(@RequestBody request: SaveDeviceTokenRequest, authentication: Authentication) {
        val user = authentication.principal as Jwt
        val userId = user.getClaimAsString("email")
        pushNotificationService.saveDeviceTokenByUserId(userId, request.deviceToken)
    }
}

data class NotificationRequest(val deviceToken: String, val message: String)

data class SaveDeviceTokenRequest(val deviceToken: String)