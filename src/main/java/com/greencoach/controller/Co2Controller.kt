package com.greencoach.controller

import com.greencoach.model.Co2SnapshotDto
import com.greencoach.service.Co2Service
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/co2")
class Co2Controller (
    private val co2Service: Co2Service
) {

    @GetMapping("/world", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun world(): Mono<Co2SnapshotDto> = co2Service.getWorld()

    @GetMapping("/korea", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun korea(): Mono<Co2SnapshotDto> = co2Service.getKorea()
}