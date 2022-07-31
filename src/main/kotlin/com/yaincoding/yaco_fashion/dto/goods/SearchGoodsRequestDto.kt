package com.yaincoding.yaco_fashion.dto.goods

data class SearchGoodsRequestDto (
    val query: String?,
    val sort: String = "MATCH"
)
