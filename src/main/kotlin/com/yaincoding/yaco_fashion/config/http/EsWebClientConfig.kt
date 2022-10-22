package com.yaincoding.yaco_fashion.config.http

import io.netty.channel.ChannelOption
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class EsWebClientConfig(
    @Value("\${elasticsearch.host}") private val host: String,
    @Value("\${elasticsearch.port}") private val port: String
) {
    @Bean
    fun webClient(): WebClient {

        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
            .responseTimeout(Duration.ofMillis(3000))

        return WebClient.builder()
            .baseUrl("http://$host:$port")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }

}