package com.greencoach.controller

import com.greencoach.model.community.CommunityNotificationDto
import com.greencoach.model.community.CommunityNotificationMetaDto
import com.greencoach.service.CommunityService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/community")
class CommunityController(
    private val communityService: CommunityService
) {
    /** 알림 목록 (최신순) */
    @GetMapping("/notifications")
    fun getNotifications(): ResponseEntity<List<CommunityNotificationDto>> =
        ResponseEntity.ok(communityService.getNotifications())

    /** 배지 메타 (로그인 여부 + 미확인 개수) */
    @GetMapping("/notifications/meta")
    fun getNotificationMeta(): ResponseEntity<CommunityNotificationMetaDto> {
        val isLoggedIn = true // TODO: SecurityContext 등으로 대체
        return ResponseEntity.ok(communityService.getNotificationMeta(isLoggedIn))
    }

    /** 전체 읽음 */
    @PostMapping("/notifications/read-all")
    fun readAll(): ResponseEntity<CommunityNotificationMetaDto> {
        val isLoggedIn = true
        return ResponseEntity.ok(communityService.readAllNotifications(isLoggedIn))
    }

    /** 단건 읽음 */
    @PostMapping("/notifications/read")
    fun readOne(@RequestParam id: Long): ResponseEntity<CommunityNotificationMetaDto> {
        val isLoggedIn = true
        return ResponseEntity.ok(communityService.readNotification(id, isLoggedIn))
    }

    /** 단건 삭제 */
    @DeleteMapping("/notifications/{id}")
    fun deleteOne(@PathVariable id: Long): ResponseEntity<CommunityNotificationMetaDto> {
        val isLoggedIn = true
        return ResponseEntity.ok(communityService.deleteNotification(id, isLoggedIn))
    }

}