package com.yaincoding.yaco_fashion.admin.es_index.goods.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.yaincoding.yaco_fashion.admin.es_index.goods.config.EsIndexQuery
import com.yaincoding.yaco_fashion.admin.es_index.goods.dto.CreateIndexResponseDto
import com.yaincoding.yaco_fashion.domain.category.entity.Category
import com.yaincoding.yaco_fashion.domain.category.repository.CategoryRepository
import com.yaincoding.yaco_fashion.domain.goods.entity.Goods
import com.yaincoding.yaco_fashion.domain.goods.repository.GoodsRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class GoodsIndexService(
    @Value("\${elasticsearch.index.goods}") private val alias: String = "goods",
    private val webClient: WebClient,
    private val categoryRepository: CategoryRepository,
    private val goodsRepository: GoodsRepository,
    private val objectMapper: ObjectMapper,
) {

    private fun createIndex(): String {

        val tz: TimeZone = TimeZone.getTimeZone("Asia/Seoul")
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.KOREA).withZone(tz.toZoneId())
        val suffix: String = LocalDateTime.now().format(formatter)
        val newIndexName = "${alias}_${suffix}"

        val responseBody: CreateIndexResponseDto? = webClient
            .put()
            .uri("/${newIndexName}")
            .bodyValue(EsIndexQuery.CREATE_GOODS_INDEX.queryDsl)
            .retrieve()
            .bodyToMono(CreateIndexResponseDto::class.java)
            .block()

        responseBody?.let {
            if (it.acknowledged) {
                return newIndexName
            }
        }

        throw Exception()
    }

    private fun indexData(indexName: String) {

        fun getCategoryIdNameMap(): Map<Long, String> {
            val categoryList: List<Category> = categoryRepository.findAll().toList()
            val map: MutableMap<Long, String> = mutableMapOf()
            categoryList.forEach { category ->
                map[category.id!!] = category.name
            }

            return map.toMap()
        }

        fun bulkIndex(indexName: String, docs: List<String>) {
            val body: String = docs.joinToString("\n") + "\n"

            val response: String? = webClient
                .post()
                .uri("${indexName}/_bulk")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

            val map: Map<String, Any> = objectMapper.readValue(
                response, object : TypeReference<Map<String, Any>>() {}
            )

            println("{took: ${map["took"]}, error: ${map["error"]}}")
        }

        val goodsList: List<Goods> = goodsRepository.findAll().toList()
        val docs: MutableList<String> = mutableListOf()
        val categoryIdNameMap = getCategoryIdNameMap()
        for (goods in goodsList) {
            val doc: StringBuilder = StringBuilder()
            doc.append("{\"index\": {\"_index\": \"$indexName\", \"_id\": ${goods.id}}}\n")
            doc.append("{")
            doc.append("\"id\": ${goods.id},")
            doc.append("\"title\": \"${goods.title}\",")
            doc.append("\"category_id\": ${goods.category.id},")
            doc.append("\"parent_category_id\": ${goods.category.parent_id},")
            doc.append("\"category_name\": \"${categoryIdNameMap[goods.category.id]}\",")
            doc.append("\"parent_category_name\": \"${categoryIdNameMap[goods.category.parent_id]}\",")
            doc.append("\"image_url\": \"${goods.imageUrl}\",")
            doc.append("\"click_count\": ${goods.clickCount},")
            doc.append("\"like_count\": ${goods.likeCount},")
            doc.append("\"sell_count\": ${goods.sellCount},")
            doc.append("\"gender\": \"${goods.gender}\",")
            goods.hashTags?.let {
                doc.append("\"hash_tags\": [${
                    goods.hashTags?.split("#")?.map { it.trim() }?.filter { it.isNotBlank() }
                        ?.joinToString(",") { "\"${it}\"" }
                }],")
            }
            doc.append("\"price\": ${goods.price},")
            doc.append("\"link\": \"${goods.link}\"")
            doc.append("}")
            docs.add(doc.toString())
            if (docs.size >= 50) {
                bulkIndex(indexName, docs.toList())
                docs.clear()
            }
        }

        if (docs.size > 0) {
            bulkIndex(indexName, docs.toList())
        }
    }

    private fun switchAlias(newIndexName: String) {

        val getAliasResponse: String? = webClient
            .get()
            .uri("/_alias/${alias}")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val map: Map<String, Any> = objectMapper.readValue(
            getAliasResponse, object : TypeReference<Map<String, Any>>() {}
        )

        val indices: Set<String> = map.keys
        println("indices=${indices}")

        val actions: MutableList<String> = mutableListOf()
        actions.add(
            """
            {
                "add": {
                    "index": "$newIndexName",
                    "alias": "$alias"
                }
            }
        """
        )

        for (oldIndex in indices) {
            actions.add(
                """
                {
                    "remove": {
                        "index": "$oldIndex",
                        "alias": "$alias"
                    }
                }
            """
            )
        }

        val body = """
            {
                "actions": [
                    ${actions.joinToString(",")}
                ]
            }
        """.trimIndent()

        webClient
            .post()
            .uri("/_aliases")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        for (index in indices) {
            webClient.delete().uri("/$index").retrieve().bodyToMono(String::class.java).block()
        }
    }

    fun reindex() {
        val newIndexName: String = createIndex()
        indexData(newIndexName)
        switchAlias(newIndexName)
    }
}