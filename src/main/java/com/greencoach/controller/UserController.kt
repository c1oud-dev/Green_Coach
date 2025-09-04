package com.greencoach.controller

import com.greencoach.model.auth.UserProfileDto
import com.greencoach.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/me")
    fun me(): ResponseEntity<UserProfileDto> =
        ResponseEntity.ok(userService.getMe())

    data class UpdateReq(val nickname: String? = null, val birth: String? = null, val gender: String? = null)

    @PatchMapping("/me")
    fun update(@RequestBody req: UpdateReq): ResponseEntity<UserProfileDto> =
        ResponseEntity.ok(userService.updateMe(req.nickname, req.birth, req.gender))
}
