package com.greencoach.repository

import com.greencoach.model.user.PasswordResetCode
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PasswordResetCodeRepository : JpaRepository<PasswordResetCode, Long> {
    fun findTopByEmailOrderByIdDesc(email: String): Optional<PasswordResetCode>
    fun deleteByEmail(email: String)
}
