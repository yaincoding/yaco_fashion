package com.yaincoding.yaco_fashion.admin.es_index.top_keyword.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.yaincoding.yaco_fashion.domain.top_keyword.entity.TopKeyword
import com.yaincoding.yaco_fashion.domain.top_keyword.repository.TopKeywordRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class TopKeywordIndexService(
    @Value("\${elasticsearch.index.top_keyword}") private val alias: String = "top_keyword",
    private val objectMapper: ObjectMapper,
    private val webClient: WebClient,
    private val topKeywordRepository: TopKeywordRepository,
) {

    private fun createIndex(): String {

        val createIndexQuery = """
            {
                "settings": {
                    "number_of_shards": 1,
                    "number_of_replicas": 0,
                    "index.max_ngram_diff": 30,
                    "analysis": {
                        "char_filter": {
                            "clean_char_filter": {
                                "pattern": "[^A-Za-z가-힣0-9]",
                                "type": "pattern_replace",
                                "replacement": " "
                            },
                            "white_remove_char_filter": {
                                "type": "pattern_replace",
                                "pattern": "\\s+",
                                "replacement": ""
                            }
                        },
                        "filter": {
                            "ngram_filter": {
                                "type": "ngram",
                                "min_gram": 1,
                                "max_gram": 30
                            }
                        },
                        "analyzer": {
                            "ngram_analyzer": {
                                "type": "custom",
                                "char_filter": [
                                    "clean_char_filter",
                                    "white_remove_char_filter"
                                ],
                                "tokenizer": "keyword",
                                "filter": [
                                    "lowercase",
                                    "hanhinsam_jamo",
                                    "ngram_filter"
                                ]
                            },
                            "no_blank_search_analyzer": {
                                "type": "custom",
                                "char_filter": [
                                    "clean_char_filter",
                                    "white_remove_char_filter"
                                ],
                                "tokenizer": "keyword",
                                "filter": [
                                    "lowercase",
                                    "hanhinsam_jamo"
                                ]
                            }
                        }
                    }
                },
                "mappings": {
                    "properties": {
                        "id": {
                            "type": "integer"
                        },
                        "keyword": {
                            "type": "keyword",
                            "fields": {
                                "ngram": {
                                    "type": "text",
                                    "analyzer": "ngram_analyzer",
                                    "search_analyzer": "no_blank_search_analyzer"
                                }
                            }
                        }
                    }
                }
            }
        """

        val tz: TimeZone = TimeZone.getTimeZone("Asia/Seoul")
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.KOREA).withZone(tz.toZoneId())
        val suffix: String = LocalDateTime.now().format(formatter)
        val newIndexName = "${alias}_${suffix}"

        val response: String? = webClient
            .put()
            .uri("/${newIndexName}")
            .bodyValue(createIndexQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        return newIndexName
    }

    private fun indexData(indexName: String) {

        fun bulkIndex(indexName: String, docs: List<String>) {

            val body: String = docs.joinToString("\n") + "\n"
            webClient
                .post()
                .uri("${indexName}/_bulk")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
        }

        val keywordList: List<TopKeyword> = topKeywordRepository.findAll().toList()
        val docs: MutableList<String> = mutableListOf()
        for (keyword in keywordList) {
            val doc: StringBuilder = StringBuilder()
            doc.append("{\"index\": {\"_index\": \"$indexName\", \"_id\": ${keyword.id}}}\n")
            doc.append("{")
            doc.append("\"keyword\": \"${keyword.keyword}\"")
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

        val indices: MutableSet<String> = mutableSetOf()
        val response: String? = webClient
            .get()
            .uri("/_alias/${alias}")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val obj: JsonNode = objectMapper.readTree(response)
        obj.fieldNames().forEachRemaining { it -> indices.add(it) }

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
            webClient
                .delete()
                .uri("/${index}")
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
        }
    }

    fun reindex() {
        val newIndexName: String = createIndex()
        indexData(newIndexName)
        switchAlias(newIndexName)
    }
}