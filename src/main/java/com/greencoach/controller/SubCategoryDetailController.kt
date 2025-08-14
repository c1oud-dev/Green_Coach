package com.greencoach.controller

import com.greencoach.model.SubCategoryDetailDto
import com.greencoach.service.SubCategoryDetailService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/subcategories")
class SubCategoryDetailController(private val svc: SubCategoryDetailService) {
    @GetMapping("/{key}/detail")
    fun detail(@PathVariable key: String): ResponseEntity<SubCategoryDetailDto> =
        svc.getDetail(key)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
}