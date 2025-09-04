package com.greencoach.controller

import com.greencoach.model.auth.*
import com.greencoach.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody req: SignUpRequest): ResponseEntity<LoginResponse> =
        ResponseEntity.ok(authService.signUp(req))

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): ResponseEntity<LoginResponse> =
        ResponseEntity.ok(authService.login(req))

    @PostMapping("/forgot")
    fun forgot(@Valid @RequestBody req: ForgotPasswordRequest): ResponseEntity<Void> {
        authService.sendResetCode(req)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/verify")
    fun verify(@Valid @RequestBody req: VerifyCodeRequest): ResponseEntity<Void> {
        authService.verifyCode(req)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/reset")
    fun reset(@Valid @RequestBody req: ResetPasswordRequest): ResponseEntity<Void> {
        authService.resetPassword(req)
        return ResponseEntity.ok().build()
    }

}