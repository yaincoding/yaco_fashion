package com.yaincoding.yaco_fashion.domain.goods.query_dsl

class QueryDslFactory {

    companion object {
        fun createEsQuery(params: EsQueryParams): String {
            val should = buildShouldQuery(params.query).joinToString(",", "[", "]")
            val filter = buildFilterQuery(params).joinToString(",", "[", "]")
            val scoreFunctions = buildScoreFunctions().joinToString(",", "[", "]")
            val sortQuery = params.sort.getSortQuery()

            return """
                {
                    "query": {
                        "function_score": {
                            "query": {
                                "constant_score": {
                                    "filter": {
                                        "bool": {
                                            "should": $should,
                                            "filter": $filter
                                        }
                                    }
                                }
                            },
                            "functions": $scoreFunctions
                        }
                    },
                    "sort": $sortQuery
                }
            """
        }

        private fun buildShouldQuery(query: String?): List<String> {
            val should = mutableListOf<String>()
            query?.let {
                should.add(
                    """
                        {
                            "multi_match": {
                                "query": "$it",
                                "type": "cross_fields",
                                "operator": "and",
                                "analyzer": "search_analyzer",
                                "fields": [
                                    "title",
                                    "hash_tags"
                                ]
                            }
                        }
                    """
                )
            }

            return should
        }

        private fun buildFilterQuery(params: EsQueryParams): List<String> {
            val filter = mutableListOf<String>()
            params.categoryId?.let {
                filter.add(
                    """
                        {
                            "term": {
                                "category_id": $it
                            }
                        }
                    """.trimIndent()
                )
            }

            return filter
        }

        private fun buildScoreFunctions(): List<String> {
            val functions: MutableList<String> = mutableListOf()
            functions.add(
                """
                    {
                        "field_value_factor": {
                            "field": "click_count",
                            "factor": 1,
                            "missing": 1
                        }
                    }
                """
            )

            functions.add(
                """
                   {
                        "field_value_factor": {
                            "field": "like_count",
                            "factor": 2,
                            "missing": 1
                        }
                   } 
                """
            )

            functions.add(
                """
                   {
                        "field_value_factor": {
                            "field": "sell_count",
                            "factor": 3,
                            "missing": 1
                        }
                   } 
                """
            )
            return functions
        }
    }
}