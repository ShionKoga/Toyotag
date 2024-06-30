package com.sk.server.auth

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserRecord, String>

@Entity
@Table(name = "toyotag_user")
data class UserRecord(
    @Id
    val email: String,
)