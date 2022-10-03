package com.yaincoding.yaco_fashion.admin.es_index.top_keyword.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.yaincoding.yaco_fashion.domain.top_keyword.entity.TopKeyword
import com.yaincoding.yaco_fashion.domain.top_keyword.repository.TopKeywordRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException.NotFound
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class TopKeywordIndexService(
    @Value("\${elasticsearch.host}") private val host: String = "localhost",
    @Value("\${elasticsearch.port}") private val port: Int = 9200,
    @Value("\${elasticsearch.index.top_keyword}") private val alias: String = "top_keyword",
    private val restTemplate: RestTemplate,
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
        val url = "${host}:${port}/${newIndexName}"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val httpEntity: HttpEntity<String> = HttpEntity<String>(createIndexQuery, headers)
        val responseEntity: ResponseEntity<String> = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String::class.java)

        if (responseEntity.statusCode != HttpStatus.OK) {
            throw Exception()
        }

        return newIndexName
    }

    private fun indexData(indexName: String) {

        fun bulkIndex(indexName: String, docs: List<String>) {
            val url = "${host}:${port}/${indexName}/_bulk"

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val body: String = docs.joinToString("\n") + "\n"
            val httpEntity: HttpEntity<String> = HttpEntity<String>(body, headers)
            val responseEntity: ResponseEntity<String> = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String::class.java)
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

        val getAliasesUrl = "$host:$port/_alias/${alias}"
        val indices: MutableSet<String> = mutableSetOf()
        try {
            val getAliasResponse: String? = restTemplate.getForObject(getAliasesUrl, String::class.java)
            val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
            val jsonObject = gson.fromJson(getAliasResponse, JsonObject::class.java)
            indices.addAll(jsonObject.keySet())
        } catch (_: NotFound) {

        }

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

        val switchAliasesUrl = "$host:$port/_aliases"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val httpEntity: HttpEntity<String> = HttpEntity<String>(body, headers)

        val switchAliasResponse = restTemplate.postForObject(switchAliasesUrl, httpEntity, String::class.java)

        for (index in indices) {
            val deleteUrl = "$host:$port/$index"
            restTemplate.delete(deleteUrl)
        }
    }

    fun reindex() {
        val newIndexName: String = createIndex()
        indexData(newIndexName)
        switchAlias(newIndexName)
    }
}