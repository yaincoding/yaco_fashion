package com.yaincoding.yaco_fashion.search.goods.query_dsl

data class EsQueryParams (
    var query: String? = null,
    var sort: GoodsSort = GoodsSort.MATCH,
    var categoryId: Int? = null,
    var page: Int = 1,
    var size: Int = 10
)