package com.greencoach.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
class WebClientConfig (

    // application.yml에 설정했던 값들과 매핑
    @Value("\${co2.connect-timeout-ms:5000}")
    private val connectTimeoutMs: Int,

    @Value("\${co2.read-timeout-ms:5000}")
    private val readTimeoutMs: Int,
) {
    /**
     * 공용 Builder.
     * - Reactor Netty 커넥터를 붙여 타임아웃을 적용합니다.
     */
    @Bean
    fun webClientBuilder(): WebClient.Builder {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
            .responseTimeout(Duration.ofMillis(readTimeoutMs.toLong()))
            .doOnConnected { conn ->
                // ReadTimeoutHandler로 읽기 타임아웃 보강
                conn.addHandlerLast(ReadTimeoutHandler(readTimeoutMs.toLong(), TimeUnit.MILLISECONDS))
            }

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .codecs { it.defaultCodecs().maxInMemorySize(20 * 1024 * 1024) } // 20MB
    }

    /**
     * OWID 전용 WebClient.
     * - baseUrl을 주입해 /grapher/co2.csv 등 절대경로 없이 호출 가능.
     */
    @Bean
    fun owidWebClient(
        builder: WebClient.Builder,
        @Value("\${co2.base-url}") baseUrl: String
    ): WebClient = builder
        .baseUrl(baseUrl)   // e.g. https://ourworldindata.org
        .build()
}