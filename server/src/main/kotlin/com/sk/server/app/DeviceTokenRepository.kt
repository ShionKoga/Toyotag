package com.sk.server.app

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceTokenRepository: JpaRepository<DeviceTokenRecord, String>

@Entity
@Table(name = "device_token")
data class DeviceTokenRecord(
    @Id
    val deviceToken: String,
    val userId: String,
)