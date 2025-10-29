package com.greencoach.service

import com.greencoach.config.JwtTokenProvider
import com.greencoach.model.auth.*
import com.greencoach.model.user.PasswordResetCode
import com.greencoach.model.user.UserEntity
import com.greencoach.repository.PasswordResetCodeRepository
import com.greencoach.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.SecureRandom
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: JwtTokenProvider,
    private val resetRepo: PasswordResetCodeRepository,
    private val mailService: MailService
) {
    @Transactional
    fun signUp(req: SignUpRequest): LoginResponse {
        require(!userRepository.existsByEmail(req.email)) { "Email already registered" }
        require(!userRepository.existsByNickname(req.nickname)) { "Nickname already taken" }
        val user = userRepository.save(
            UserEntity(
                email = req.email,
                passwordHash = passwordEncoder.encode(req.password),
                nickname = req.nickname,
                birth = req.birth,
                gender = req.gender
            )
        )
        val token = tokenProvider.createToken(user.email)
        return LoginResponse(token)
    }

    fun login(req: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(req.email)
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials") }

        if (!passwordEncoder.matches(req.password, user.passwordHash)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }

        return LoginResponse(tokenProvider.createToken(user.email))
    }

    fun checkNickname(req: NicknameCheckRequest): NicknameCheckResponse {
        val available = !userRepository.existsByNickname(req.nickname)
        return NicknameCheckResponse(available)
    }

    @Transactional
    fun sendResetCode(req: ForgotPasswordRequest) {
        // 사용자 존재 체크 (존재하지 않아도 같은 응답을 주는 것이 보안상 안전하지만 데모에선 단순화)
        userRepository.findByEmail(req.email)
            .orElseThrow { IllegalArgumentException("No such user") }

        // 기존 코드 삭제 후 새 코드 발급
        resetRepo.deleteByEmail(req.email)
        val code = generate4Digit()
        val entity = PasswordResetCode(
            email = req.email,
            code = code,
            expiresAt = Instant.now().plus(20, ChronoUnit.MINUTES)
        )
        resetRepo.save(entity)

        mailService.sendResetCode(req.email, code)
    }

    fun verifyCode(req: VerifyCodeRequest) {
        val entity = resetRepo.findTopByEmailOrderByIdDesc(req.email)
            .orElseThrow { IllegalArgumentException("Code not found") }
        require(entity.code == req.code) { "Invalid code" }
        require(Instant.now().isBefore(entity.expiresAt)) { "Code expired" }
        // OK
    }

    @Transactional
    fun resetPassword(req: ResetPasswordRequest) {
        // 코드가 유효했는지 클라이언트가 verify 이후에만 호출한다고 가정(필요시 서버에서 한번 더 확인)
        val user = userRepository.findByEmail(req.email)
            .orElseThrow { IllegalArgumentException("User not found") }
        user.passwordHash = passwordEncoder.encode(req.newPassword)
        userRepository.save(user)
        resetRepo.deleteByEmail(req.email)
    }

    private fun generate4Digit(): String {
        val rnd = SecureRandom()
        val n = rnd.nextInt(10000)
        return String.format("%04d", n)
    }
}
