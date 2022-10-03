package com.yaincoding.yaco_fashion.search.auto_complete.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.yaincoding.yaco_fashion.search.auto_complete.dto.AutoCompleteRequestDto
import com.yaincoding.yaco_fashion.search.auto_complete.dto.AutoCompleteResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AutoCompleteService(
    @Value("\${elasticsearch.host}") private val host: String = "localhost",
    @Value("\${elasticsearch.port}") private val port: Int = 9200,
    @Value("\${elasticsearch.index.top_keyword}") private val topKeywordIndex: String = "top_keyword",
    @Autowired val restTemplate: RestTemplate
) {

    fun search(requestDto: AutoCompleteRequestDto): AutoCompleteResponseDto {
        val url = "$host:$port/$topKeywordIndex/_search"

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

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val httpEntity: HttpEntity<String> = HttpEntity<String>(esQuery, headers)
        val response: String? = restTemplate.postForObject(url, httpEntity, String::class.java)

        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val jsonObject = gson.fromJson(response, JsonObject::class.java)
        val hits = jsonObject.getAsJsonObject("hits")
        val count = hits.getAsJsonObject("total").get("value").asInt
        val docHits: JsonArray = hits.getAsJsonArray("hits")
        val keywords: List<String> = docHits.map { it.asJsonObject.getAsJsonObject("_source").get("keyword").asString }

        return AutoCompleteResponseDto(count = count, keywords = keywords)
    }
}