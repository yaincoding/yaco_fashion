package com.yaincoding.yaco_fashion.search.auto_complete.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.yaincoding.yaco_fashion.search.auto_complete.dto.AutoCompleteRequestDto
import com.yaincoding.yaco_fashion.search.auto_complete.dto.AutoCompleteResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class AutoCompleteService(
    @Value("\${elasticsearch.index.top_keyword}") private val topKeywordIndex: String = "top_keyword",
    private val objectMapper: ObjectMapper,
    private val webClient: WebClient,
) {

    fun search(requestDto: AutoCompleteRequestDto): AutoCompleteResponseDto {

        val esQuery: String = """
            {
                "size": ${requestDto.size},
                "query": {
                    "match": {
                        "keyword.ngram": "${requestDto.query}"
                    }
                }
            }
        """.trimIndent()

        val response: String? = webClient
            .post()
            .uri("/${topKeywordIndex}/_search")
            .bodyValue(esQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val jsonObject: JsonNode = objectMapper.readTree(response)
        val hits = jsonObject.get("hits")
        val count: Int = hits.get("total").get("value").asInt()
        val docHits: JsonNode = hits.get("hits")
        val keywords: List<String> = docHits.map { it.get("_source").get("keyword").asText() }

        return AutoCompleteResponseDto(count = count, keywords = keywords)
    }
}