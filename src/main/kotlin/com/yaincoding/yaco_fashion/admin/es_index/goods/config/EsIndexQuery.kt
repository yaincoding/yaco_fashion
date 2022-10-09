package com.yaincoding.yaco_fashion.admin.es_index.goods.config

enum class EsIndexQuery (
    val queryDsl: String
) {

    CREATE_GOODS_INDEX(
        """
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
    )
}