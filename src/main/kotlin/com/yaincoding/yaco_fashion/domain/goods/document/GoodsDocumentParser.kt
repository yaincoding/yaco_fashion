package com.yaincoding.yaco_fashion.domain.goods.document

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.yaincoding.yaco_fashion.domain.goods.dto.GetGoodsResponseDto
import com.yaincoding.yaco_fashion.domain.goods.dto.SearchGoodsResponseDto

class GoodsDocumentParser {

    companion object {
        fun parseGetGoodsResponse(response: String): GetGoodsResponseDto {
            val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
            val jsonObject = gson.fromJson(response, JsonObject::class.java)
            val doc = jsonObject.getAsJsonObject("_source")

            return gson.fromJson(doc.toString(), GetGoodsResponseDto::class.java)
        }

        fun parseSearchGoodsResponse(response: String): SearchGoodsResponseDto {

            val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
            val jsonObject = gson.fromJson(response, JsonObject::class.java)
            val hits = jsonObject.getAsJsonObject("hits")
            val count = hits.getAsJsonObject("total").get("value").asInt
            val docHits: JsonArray = hits.getAsJsonArray("hits")
            val docsString = docHits.map { it.asJsonObject.getAsJsonObject("_source") }.toString()

            val type = object : TypeToken<List<GoodsDocument>>() {}.type
            val docs: List<GoodsDocument> = gson.fromJson(docsString, type)

            return SearchGoodsResponseDto(count=count, docs=docs)
        }
    }
}