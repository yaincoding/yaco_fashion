package com.yaincoding.yaco_fashion.query_dsl

enum class GoodsSort {
    MATCH {
        override fun getSortQuery(): String {
            return """
                [
                    {
                        "_score": {
                            "order": "desc"
                        }
                    }
                ]
            """
        }
    },
    CLICK {
        override fun getSortQuery(): String {
            return """
                [
                    {
                        "click_count": {
                            "order": "desc"
                        }
                    }
                ]
            """.trimIndent()
        }
    },
    SELL {
        override fun getSortQuery(): String {
            return """
                [
                    {
                        "sell_count": {
                            "order": "desc"
                        }
                    }
                ]
            """.trimIndent()
        }
    },
    LIKE {
        override fun getSortQuery(): String {
            return """
                [
                    {
                        "like_count": {
                            "order": "desc"
                        }
                    }
                ]
            """.trimIndent()
        }
    },
    PRICE_HIGH {
        override fun getSortQuery(): String {
            return """
                [
                    {
                        "price": {
                            "order": "desc"
                        }
                    }
                ]
            """.trimIndent()
        }
    },
    PRICE_LOW {
        override fun getSortQuery(): String {
            return """
                [
                    {
                        "price": {
                            "order": "asc"
                        }
                    }
                ]
            """.trimIndent()
        }
    };

    abstract fun getSortQuery(): String
}