package com.yaincoding.yaco_fashion.dto.goods

import com.yaincoding.yaco_fashion.document.goods.GoodsDocument

data class SearchGoodsResponseDto (
    val count: Int,
    val docs: List<GoodsDocument>
)