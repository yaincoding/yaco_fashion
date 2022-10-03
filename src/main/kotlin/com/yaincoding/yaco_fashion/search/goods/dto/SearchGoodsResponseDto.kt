package com.yaincoding.yaco_fashion.search.goods.dto

import com.yaincoding.yaco_fashion.search.goods.document.GoodsDocument

data class SearchGoodsResponseDto (
    val count: Int,
    val docs: List<GoodsDocument>
)