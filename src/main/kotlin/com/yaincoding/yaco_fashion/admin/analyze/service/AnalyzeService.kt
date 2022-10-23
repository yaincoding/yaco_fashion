package com.yaincoding.yaco_fashion.admin.analyze.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.yaincoding.yaco_fashion.admin.analyze.dto.TokenDto
import com.yaincoding.yaco_fashion.admin.analyze.dto.TokenListDto
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class AnalyzeService(
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper,
) {

    fun analyze(query: String, analyzer: String): TokenListDto {

        val esQuery = """
            {
                "analyzer": "$analyzer",
                "text": "$query"
            }
        """

        val response: String = webClient
            .post()
            .uri("/goods/_analyze")
            .bodyValue(esQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()!!

        return parseAnalyzeResponse(response)
    }

    private fun parseAnalyzeResponse(response: String): TokenListDto {
        val obj: JsonNode = objectMapper.readTree(response)
        val tokensArray: JsonNode = obj.get("tokens")
        val tokens: List<TokenDto> = tokensArray.map { objectMapper.readValue(it.toString(), TokenDto::class.java) }

        return TokenListDto(tokens = tokens)
    }
}