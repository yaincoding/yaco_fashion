package com.yaincoding.yaco_fashion.query_dsl

import com.yaincoding.yaco_fashion.dto.goods.SearchGoodsRequestDto

class QueryDslFactory {

    companion object {
        fun createEsQuery(params: EsQueryParams): String {
            val should = buildShould(params.query)
            val esQuery = """
                {
                    "query": {
                        "bool": {
                            "should": ${should.joinToString(",", "[", "]")},
                            "minimum_should_match": 1
                        }
                    }
                }
            """

            return esQuery
        }

        fun buildShould(query: String?): List<String> {
            val should = mutableListOf<String>()
            query?.let {
                should.add(
                    """
                        {
                            "match": {
                                "title": "$query"
                            }
                        }
                    """
                )
            }

            return should
        }
    }
}