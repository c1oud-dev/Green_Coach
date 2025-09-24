package com.greencoach.controller

import com.greencoach.model.scan.ScanResponse
import com.greencoach.service.ScanService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/scan")
class ScanController(
    private val scanService: ScanService
) {
    @PostMapping(consumes = ["multipart/form-data"])
    fun scan(@RequestParam("file") file: MultipartFile): ResponseEntity<ScanResponse> {
        val result = scanService.analyzeImage(file)
        return ResponseEntity.ok(result)
    }
}