package com.yaincoding.yaco_fashion.search.goods.document

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.yaincoding.yaco_fashion.search.goods.dto.GetGoodsResponseDto
import com.yaincoding.yaco_fashion.search.goods.dto.SearchGoodsResponseDto
import org.springframework.stereotype.Component

@Component
class GoodsDocumentParser(
    private val objectMapper: ObjectMapper
) {

    fun parseGetGoodsResponse(response: String): GoodsDocument {
        val obj: JsonNode = objectMapper.readTree(response)
        return objectMapper.readValue(obj.get("_source").toString(), GoodsDocument::class.java)
    }

    fun parseSearchGoodsResponse(response: String): SearchGoodsResponseDto {
        val obj: JsonNode = objectMapper.readTree(response)
        val hits: JsonNode = obj.get("hits")
        val count: Int = hits.get("total").get("value").asInt()
        val docHits: JsonNode = hits.get("hits")
        val docs: List<GoodsDocument> = docHits.map { objectMapper.readValue(it.get("_source").toString(), GoodsDocument::class.java) }

        return SearchGoodsResponseDto(count=count, docs=docs)
    }
}