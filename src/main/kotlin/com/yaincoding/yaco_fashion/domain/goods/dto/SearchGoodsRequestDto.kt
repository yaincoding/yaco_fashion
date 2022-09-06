package com.yaincoding.yaco_fashion.domain.goods.dto

data class SearchGoodsRequestDto (
    val query: String?,
    val sort: String = "MATCH",
    val categoryId: Int? = null,
    val page: Int = 1,
    val size: Int = 10,
)
