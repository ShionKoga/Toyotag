package com.sk.server

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
}

data class NotificationRequest(val deviceToken: String, val message: String)