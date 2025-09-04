package com.greencoach.model.user

import jakarta.persistence.*
import java.time.LocalDate

@Entity @Table(name = "users")
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(nullable = false)
    var passwordHash: String,

    @Column(nullable = false)
    var nickname: String,

    var birth: LocalDate? = null,
    var gender: String? = null,
    var verified: Boolean = true,   // 이메일 인증 플로우가 없으니 기본 true
    var avatarUrl: String? = null
)
