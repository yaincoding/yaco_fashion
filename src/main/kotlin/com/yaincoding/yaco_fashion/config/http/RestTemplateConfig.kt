package com.yaincoding.yaco_fashion.config.http

import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {

    @Bean
    private fun restTemplate(): RestTemplate {
        val httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(25)
                .build()

        val factory = HttpComponentsClientHttpRequestFactory(httpClient)
        factory.setConnectTimeout(10000)
        factory.setReadTimeout(10000)

        return RestTemplate(factory)
    }
}