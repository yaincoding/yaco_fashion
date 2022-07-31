package com.yaincoding.yaco_fashion.query_dsl

class QueryDslFactory {

    companion object {
        fun createEsQuery(params: EsQueryParams): String {
            val should = buildShould(params.query).joinToString(",", "[", "]")
            val scoreFunctions = buildScoreFunctions().joinToString(",", "[", "]")
            val sortQuery = params.sort.getSortQuery()

            val esQuery = """
                {
                    "query": {
                        "function_score": {
                            "query": {
                                "constant_score": {
                                    "filter": {
                                        "bool": {
                                            "should": $should
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

            return esQuery
        }

        private fun buildShould(query: String?): List<String> {
            val should = mutableListOf<String>()
            query?.let {
                should.add(
                    """
                        {
                            "multi_match": {
                                "query": "$query",
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