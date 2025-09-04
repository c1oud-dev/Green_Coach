package com.greencoach.model.user

import jakarta.persistence.*
import java.time.Instant

@Entity @Table(name = "password_reset_codes")
class PasswordResetCode(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false, length = 4)
    var code: String,

    @Column(nullable = false)
    var expiresAt: Instant
)
