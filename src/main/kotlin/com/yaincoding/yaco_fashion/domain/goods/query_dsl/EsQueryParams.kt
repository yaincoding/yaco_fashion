package com.yaincoding.yaco_fashion.domain.goods.query_dsl

data class EsQueryParams (
    var query: String? = null,
    var sort: GoodsSort = GoodsSort.MATCH,
    var categoryId: Int? = null
)