package com.yaincoding.yaco_fashion.config.aws

import com.amazonaws.regions.Regions
import com.amazonaws.services.opensearch.AmazonOpenSearch
import com.amazonaws.services.opensearch.AmazonOpenSearchClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpensearchConfig {
    @Bean
    fun createOpensearchClient(): AmazonOpenSearch {
        return AmazonOpenSearchClient.builder()
            .withRegion(Regions.AP_NORTHEAST_2)
            .build()
    }
}