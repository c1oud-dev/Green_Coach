package com.greencoach.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MailService {
    private val log = LoggerFactory.getLogger(javaClass)

    fun sendResetCode(email: String, code: String) {
        // 실제 적용 시 JavaMailSender 등으로 교체
        log.info("[MockMail] send code {} to {}", code, email)
    }
}