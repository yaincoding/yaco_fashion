package com.yaincoding.yaco_fashion.domain.goods.dto

import com.yaincoding.yaco_fashion.domain.goods.document.GoodsDocument

data class SearchGoodsResponseDto (
    val count: Int,
    val docs: List<GoodsDocument>
)