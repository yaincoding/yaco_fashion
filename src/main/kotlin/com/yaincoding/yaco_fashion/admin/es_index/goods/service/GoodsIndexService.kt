package com.yaincoding.yaco_fashion.admin.es_index.goods.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.yaincoding.yaco_fashion.domain.category.entity.Category
import com.yaincoding.yaco_fashion.domain.category.repository.CategoryRepository
import com.yaincoding.yaco_fashion.domain.goods.entity.Goods
import com.yaincoding.yaco_fashion.domain.goods.repository.GoodsRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class GoodsIndexService(
    @Value("\${elasticsearch.host}") private val host: String = "localhost",
    @Value("\${elasticsearch.port}") private val port: Int = 9200,
    @Value("\${elasticsearch.index.goods}") private val alias: String = "goods",
    private val restTemplate: RestTemplate,
    private val categoryRepository: CategoryRepository,
    private val goodsRepository: GoodsRepository,
) {

    private fun createIndex(): String {

        val createIndexQuery = """
            {
                "settings": {
                    "number_of_shards": 1,
                    "number_of_replicas": 0,
                    "analysis": {
                        "char_filter": {
                            "clean_filter": {
                                "pattern": "[^A-Za-z가-힣0-9]",
                                "type": "pattern_replace",
                                "replacement": " "
                            }
                        },
                        "tokenizer": {
                            "kor_tokenizer": {
                                "type": "nori_tokenizer",
                                "decompound_mode": "discard",
                                "discard_punctuation": "true",
                                "user_dictionary": "user_dictionary.txt"
                            }
                        },
                        "filter": {
                            "synonym_filter": {
                                "type": "synonym_graph",
                                "synonyms_path": "synonyms.txt"
                            }
                        },
                        "analyzer": {
                            "index_analyzer": {
                                "type": "custom",
                                "char_filter": [
                                    "clean_filter"
                                ],
                                "tokenizer": "kor_tokenizer",
                                "filter": [
                                    "lowercase",
                                    "nori_readingform"
                                ]
                            },
                            "search_analyzer": {
                                "type": "custom",
                                "char_filter": [
                                    "clean_filter"
                                ],
                                "tokenizer": "kor_tokenizer",
                                "filter": [
                                    "lowercase",
                                    "nori_readingform",
                                    "synonym_filter"
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
                        "title": {
                            "type": "text",
                            "analyzer": "index_analyzer",
                            "search_analyzer": "search_analyzer"
                        },
                        "category_id": {
                            "type": "integer"
                        },
                        "category_name": {
                            "type": "text",
                            "fields": {
                                "keyword": {
                                    "type": "keyword"
                                }
                            }
                        },
                        "parent_category_id": {
                            "type": "integer"
                        },
                        "parent_category_name": {
                            "type": "text",
                            "fields": {
                                "keyword": {
                                    "type": "keyword"
                                }
                            }
                        },
                        "image_url": {
                            "type": "keyword"
                        },
                        "click_count": {
                            "type": "integer"
                        },
                        "sell_count": {
                            "type": "integer"
                        },
                        "like_count": {
                            "type": "integer"
                        },
                        "gender": {
                            "type": "keyword"
                        },
                        "hash_tags": {
                            "type": "text",
                            "analyzer": "index_analyzer",
                            "search_analyzer": "search_analyzer"
                        },
                        "price": {
                            "type": "integer"
                        },
                        "link": {
                            "type": "keyword"
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

        fun getCategoryIdNameMap(): Map<Long, String> {
            val categoryList: List<Category> = categoryRepository.findAll().toList()
            val map: MutableMap<Long, String> = mutableMapOf()
            categoryList.forEach { category ->
                map[category.id!!] = category.name
            }

            return map.toMap()
        }

        fun bulkIndex(indexName: String, docs: List<String>) {
            val url = "${host}:${port}/${indexName}/_bulk"

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val body: String = docs.joinToString("\n") + "\n"
            val httpEntity: HttpEntity<String> = HttpEntity<String>(body, headers)
            val responseEntity: ResponseEntity<String> = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String::class.java)
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

        val getAliasesUrl = "$host:$port/_alias/${alias}"
        val getAliasResponse: String? = restTemplate.getForObject(getAliasesUrl, String::class.java)
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val jsonObject = gson.fromJson(getAliasResponse, JsonObject::class.java)
        val indices: Set<String> = jsonObject.keySet()

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